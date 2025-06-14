const express = require('express');
const mysql = require('mysql');
const cron = require('node-cron');

const app = express();
app.use(express.json());

const jwt = require('jsonwebtoken');

const crypto = require('crypto');

const nodemailer = require('nodemailer');

let transporter = nodemailer.createTransport({
  host: "smtp.gmail.com",
  port: 587,
  secure: false, // true: 465, false: X
  auth: {
    user: 'pillulebox@gmail.com',
    pass: 'mbab bkcm dggc noax'  // Application pass
  }
});

const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: '',
  database: 'pillulebox'
});

connection.connect((err) => {
  if (err) {
    console.error('Error al conectar a la base de datos: ', err);
    return;
  }
  console.log('Conectado a la base de datos MySQL');
});

app.post('/validate_mac', (req, res) => {
  const { mac_address } = req.body;
  console.log("Validando dirección MAC: " + mac_address);

  const query = 'SELECT * FROM dispenser WHERE mac = ?';
  connection.query(query, [mac_address], (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al verificar dispositivo' });
      return;
    }
    if (results.length > 0) {
      res.json({ validated: true, message: 'Dispositivo autorizado' });
      console.log("Dispositivo autorizado");
    } else {
      res.status(401).json({ validated: false, error: 'Dispositivo no autorizado' });
      console.log("Dispositivo no autorizado");
    }
  });
});

app.post('/login', (req, res) => {
  const { username_email, password, secretKey, mac_address } = req.body;
  console.log("\n\nAutenticando: '" + username_email + "' & '" + password + "'" + " | secret key: " + secretKey + " | MAC: " + mac_address);
  
  const decryptedPasswordApp = decryptPassword(password);
  const queryPass = 'SELECT password FROM user WHERE username = ? OR email = ?';
  connection.query(queryPass, [username_email, username_email], (err, results) => {
    if (err) {
      console.error(err);
      return;
    }
    if (results.length > 0) {
      const passDB = results[0].password;
      const decryptedPasswordDB = decryptPassword(passDB);
      console.log("Contraseñas:\nBD: " + decryptedPasswordDB + "\nApp: " + decryptedPasswordApp);
    } else {
      console.log("no coinciden las credenciales");
    }
  });


  const query = 'SELECT id FROM user WHERE (username = ? AND password = ?) OR (email = ? AND password = ?)';
  connection.query(query, [username_email, password, username_email, password], (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al buscar el usuario' });
      return;
      }
    if (results.length > 0) {
      console.log("\nUsuario autenticado");
      const id = results[0].id;

      if(mac_address) {
        const updateQuery = 'UPDATE dispenser SET user_id = ? WHERE mac = ?';
        connection.query(updateQuery, [id, mac_address], (updateErr) => {
          if (updateErr) {
            console.error(updateErr);
            return res.status(500).json({ error: 'Error al actualizar el dispenser' });
          }
          console.log("Dispenser actualizado");
        });
    }

      // Generación de token
      const payload = {
        username_email,
        password
      };
      const options = {
        expiresIn: '1w' // Expiración "w" : semanas, "m" : minutos
      };
      const token = jwt.sign(payload, secretKey, options);
      const decodedToken = jwt.decode(token);
      const expirationNumber = decodedToken.exp;

      const queryToken = 'INSERT INTO tokens (user, token, token_exp) VALUES (?, ?, ?)';
      connection.query(queryToken, [id, token, expirationNumber], (err, resultToken) => {
        const expirationDate = new Date(expirationNumber * 1000);
        console.log("\n\nDatos:\ntoken: " + token + "\nExpirationDate: " + expirationDate.toLocaleString() + "\nid: " + id);
        if (err) {
          console.log(err);
          res.status(500).json({ error: 'Error al registrar el token' });
          return;
        }
        if (resultToken.affectedRows > 0) {
          res.json({ token });
          console.log(":)");
        }
        else {
          res.status(500).json({ error: 'Error al actualizar campos' });
          console.log("Error al actualizar campos");
        }
      });
    } else {
      res.status(401).json({ error: 'Usuario o contraseña incorrectos' });
      console.log(":(");
    }
  });
});

app.post('/validate_token', (req, res) => {
  const { token, mac_address } = req.body;
  console.log("\nValidando token: " + token);

  const query = `
    SELECT t.id, t.token_exp, u.username, u.email
    FROM tokens t
    JOIN user u ON t.user = u.id
    WHERE t.token = ? AND t.token_exp > ?
  `;
  const currentTimestamp = Math.floor(Date.now() / 1000);

  const currentDate = new Date(currentTimestamp * 1000);

  connection.query(query, [token, currentTimestamp], (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ validated: false, error: 'Error al verificar el token' });
      return;
    }
    if (results.length > 0) {
      const expirationDate = new Date(results[0].token_exp * 1000);
      const username = results[0].username;
      const email = results[0].email;

      console.log("Token válido");
      console.log("Fecha actual:", currentDate.toLocaleString());
      console.log("Fecha de expiración:", expirationDate.toLocaleString());
      console.log("Tiempo restante:", Math.floor((expirationDate - currentDate) / 1000) + " segundos");

      res.json({
        validated: true,
        username: username,
        email: email
      });
    } else {
      if(mac_address) {
        const updateQuery = 'UPDATE dispenser SET user_id = NULL WHERE mac = ?';
        connection.query(updateQuery, [ mac_address], (updateErr) => {
          if (updateErr) {
            console.error(updateErr);
            return res.status(500).json({ error: 'Error al actualizar el dispenser' });
          }
          console.log("Dispenser actualizado");
        });
    }
      console.log("Token inválido o expirado");
      console.log("Fecha actual:", currentDate.toLocaleString());
      res.status(401).json({ validated: false, error: 'Token inválido o expirado' });
    }
  });
});

app.post('/validate_fields', (req, res) => {
  const { username, email } = req.body;
  console.log("Validando: '" + username + "' & '" + email + "'");

  const checkUsernameQuery = 'SELECT * FROM user WHERE username = ?';
  connection.query(checkUsernameQuery, [username], (err, usernameResults) => {
    if (err) {
      console.error(err);
      console.log(err);
      res.status(500).json({ error: 'Error al verificar el nombre de usuario' });
      return;
    }
    const checkEmailQuery = 'SELECT * FROM user WHERE email = ?';
    connection.query(checkEmailQuery, [email], (err, emailResults) => {
      if (err) {
        console.error(err);
        console.log(err);
        res.status(500).json({ error: 'Error al verificar el correo electrónico' });
        return;
      }
      if (usernameResults.length > 0) {
        console.log(":(");
        res.status(409).json({ error: 'El nombre de usuario ya está en uso' });
        return;
      }
      if (emailResults.length > 0) {
        console.log(":(");
        res.status(409).json({ error: 'El correo electrónico ya está en uso' });
        return;
      }
      res.json({ message: 'El correo electrónico ya está en uso', validated: true });
    });
  });
});

app.post('/signup', (req, res) => {
  const { username, email, password } = req.body;
  console.log("Registrando: '" + username + "' & '" + email + "' & '" + password + "'");

  const query = 'INSERT INTO user (username, email, password) VALUES (?, ?, ?)';
  connection.query(query, [username, email, password], (err) => {
    if (err) {
      console.error(err);
      console.log(err);
      res.status(500).json({ error: 'Error al registrar el usuario' });
      return;
    }
    res.json({ message: 'Usuario registrado', validated: true });
    console.log(":)");
  });
});

app.post('/sendCode', (req, res) => {
  const { code, email } = req.body;
  const createdAt = new Date();
  const expiresAt = new Date(createdAt.getTime() + (5 * 60 * 1000));

  const queryDelete = 'DELETE FROM codes WHERE (email = ?)';
  connection.query(queryDelete, [email], (err) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error' });
      return;
    }
  });

  let mailOptions = {
    from: '"PilluleBox" <pillulebox@gmail.com>',
    to: email,
    subject: 'Código de verificación',
    text: `Tu código de verificación es: ${code}`,
    html: `<b>Tu código de verificación es: ${code}</b>`
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.log(error);
      res.status(500).json({ error: 'Error al enviar el correo electrónico' });
    } else {
      console.log('Correo enviado: ' + info.response);
      const query = 'INSERT INTO codes (code, email, creation_date, expiration_date) VALUES (?, ?, ?, ?)';
      connection.query(query, [code, email, createdAt, expiresAt], (err) => {
        if (err) {
          console.error(err);
          res.status(500).json({ error: 'Error al registrar el código' });
          return;
        } else {
          res.json({ message: 'Código registrado y correo enviado', sent: true });
          console.log('Código registrado y correo enviado');
        }
      });
    }
  });


});

app.post('/validateCode', (req, res) => {
  const { code, email } = req.body;
  const currentDate = new Date();
  console.log("Validando: '" + code + "' & '" + email + "'");

  const query = 'SELECT * FROM codes WHERE email = ? AND code = ?';

  connection.query(query, [email, code], (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al verificar el código' });
      return;
    }

    if (results.length > 0) {
      const expirationDate = new Date(results[0].expiration_date);
      console.log("BD: '" + code + "' & '" + email + "' & '" + currentDate + "'");

      if (currentDate <= expirationDate) {
        const deleteQuery = 'DELETE FROM codes WHERE email = ?';
        connection.query(deleteQuery, [email], (deleteErr) => {
          if (deleteErr) {
            console.error(deleteErr);
            console.log(deleteErr);
          }
          res.json({ message: 'Código validado correctamente', validated: true });
          console.log('Código validado correctamente');
        });
      } else {
        res.status(400).json({ error: 'Código expirado' });
        console.log('Código expirado');
      }
    } else {
      res.status(400).json({ error: 'Código inválido' });
      console.log('Código inválido');
    }
  });
});

app.post('/update_dispenser_context', authMiddleware, (req, res) => {
  const { mac_address, context } = req.body;
  const userId = req.userId;
  
  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [mac_address, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }
    
    const updateQuery = 'UPDATE dispenser SET context = ? WHERE mac = ?';
    connection.query(updateQuery, [context, mac_address], (updateErr) => {
      if (updateErr) {
        console.error('Error al actualizar el contexto:', updateErr);
        return res.status(500).json({ error: 'Error al actualizar el contexto' });
      }
      
      console.log(`Contexto actualizado para dispensador ${mac_address}: ${context}`);
      res.json({ message: 'Contexto actualizado correctamente' });
    });
  });
});

app.post('/add_single_mode/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { medicine_name, dispensing_date } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const insertQuery = 'INSERT INTO single_mode (mac, medicine_name, dispensing_date) VALUES (?, ?, ?)';
    connection.query(insertQuery, [macAddress, medicine_name, dispensing_date], (insertErr, result) => {
      if (insertErr) {
        console.error('Error al añadir el modo:', insertErr);
        return res.status(500).json({ error: 'Error al añadir el modo' });
      }
      
      res.json({ message: 'Modo añadido correctamente'});
    });
  });
});

app.post('/add_sequential_mode/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { 
    medicine_name, 
    start_date, 
    end_date, 
    period,
    limit_times_consumption,
    affected_periods,
    current_times_consumption 
  } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const insertQuery = `
      INSERT INTO sequential_mode (
        mac,
        medicine_name, 
        start_date,
        end_date,
        period,
        limit_times_consumption,
        affected_periods,
        current_times_consumption
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    connection.query(insertQuery, [
      macAddress,
      medicine_name,
      start_date,
      end_date,
      period,
      limit_times_consumption,
      affected_periods,
      current_times_consumption
    ], (insertErr, result) => {
      if (insertErr) {
        console.error('Error al añadir el modo secuencial:', insertErr);
        return res.status(500).json({ error: 'Error al añadir el modo secuencial' });
      }
      
      res.json({ 
        message: 'Modo secuencial añadido correctamente',
        id: result.insertId 
      });
    });
  });
});

app.post('/add_basic_mode/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { 
    medicine_name, 
    morning_start_time,
    morning_end_time,
    afternoon_start_time,
    afternoon_end_time,
    night_start_time,
    night_end_time
  } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const insertQuery = `
      INSERT INTO basic_mode (
        mac,
        medicine_name,
        morning_start_time,
        morning_end_time,
        afternoon_start_time,
        afternoon_end_time,
        night_start_time,
        night_end_time
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    connection.query(insertQuery, [
      macAddress,
      medicine_name,
      morning_start_time,
      morning_end_time,
      afternoon_start_time,
      afternoon_end_time,
      night_start_time,
      night_end_time
    ], (insertErr, result) => {
      if (insertErr) {
        console.error('Error al añadir el modo básico:', insertErr);
        return res.status(500).json({ error: 'Error al añadir el modo básico' });
      }
      
      res.json({ 
        message: 'Modo básico añadido correctamente',
        id: result.insertId 
      });
    });
  });
});

app.post('/register_history/:mac', (req, res) => {
  const macAddress = req.params.mac;
  const { 
    medicine_name, 
    consumption_status, 
    date_consumption, 
    reason,
    cell_id
  } = req.body;

  console.log(`Registrando dispensado para MAC: ${macAddress}, Medicina: ${medicine_name}, Celda ID: ${cell_id}`);
  let formattedDate;
  if (typeof date_consumption === 'number') {
    const date = new Date(date_consumption * 1000);
    formattedDate = date.toISOString().slice(0, 19).replace('T', ' ');
    console.log(`Timestamp recibido: ${date_consumption}, Fecha convertida: ${formattedDate}`);
  } else {
    formattedDate = date_consumption;
    console.log(`Fecha recibida como string: ${formattedDate}`);
  }
  
  const checkDispenserQuery = 'SELECT * FROM dispenser WHERE mac = ?';
  connection.query(checkDispenserQuery, [macAddress], (err, dispenserResults) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (dispenserResults.length === 0) {
      return res.status(404).json({ error: 'Dispensador no encontrado' });
    }
    
    const getCellQuery = `
      SELECT 
        c.id, c.num_cell, c.single_mode_id, c.sequential_mode_id, c.basic_mode_id,
        sm.id as single_id,
        sqm.id as seq_id, sqm.current_times_consumption,
        bm.id as basic_id
      FROM cell c
      LEFT JOIN single_mode sm ON c.single_mode_id = sm.id
      LEFT JOIN sequential_mode sqm ON c.sequential_mode_id = sqm.id
      LEFT JOIN basic_mode bm ON c.basic_mode_id = bm.id
      WHERE c.id = ? AND c.mac_dispenser = ?
    `;

    connection.query(getCellQuery, [cell_id, macAddress], (cellErr, cellResults) => {
      if (cellErr) {
        console.error('Error al obtener información de la celda:', cellErr);
        return res.status(500).json({ error: 'Error al obtener información de la celda' });
      }

      if (cellResults.length === 0) {
        return res.status(404).json({ error: 'Celda no encontrada' });
      }
      
      const cellInfo = cellResults[0];
      
      connection.beginTransaction(transErr => {
        if (transErr) {
          console.error('Error al iniciar transacción:', transErr);
          return res.status(500).json({ error: 'Error al procesar el dispensado' });
        }
        
        const insertHistoryQuery = `
          INSERT INTO history (mac_dispenser, medicine_name, consumption_status, date_consumption, reason)
          VALUES (?, ?, ?, ?, ?)
        `;

        connection.query(insertHistoryQuery, [
          macAddress,
          medicine_name,
          consumption_status,
          formattedDate,
          reason
        ], (historyErr, historyResult) => {
          if (historyErr) {
            return connection.rollback(() => {
              console.error('Error al insertar en historial:', historyErr);
              res.status(500).json({ error: 'Error al registrar en historial' });
            });
          }

          console.log('Historial registrado correctamente con fecha:', formattedDate);

          const clearCellQuery = 'UPDATE cell SET current_medicine_date = NULL WHERE id = ?';
          connection.query(clearCellQuery, [cell_id], (clearErr) => {
            if (clearErr) {
              return connection.rollback(() => {
                console.error('Error al limpiar la celda:', clearErr);
                res.status(500).json({ error: 'Error al limpiar la celda' });
              });
            }

            console.log(`Celda ${cell_id} limpiada (current_medicine_date = NULL)`);
            
            if (cellInfo.seq_id && consumption_status === 1) {
              const updateSequentialQuery = `
                UPDATE sequential_mode 
                SET current_times_consumption = current_times_consumption + 1
                WHERE id = ?
              `;

              connection.query(updateSequentialQuery, [cellInfo.seq_id], (updateErr) => {
                if (updateErr) {
                  return connection.rollback(() => {
                    console.error('Error al actualizar consumos secuenciales:', updateErr);
                    res.status(500).json({ error: 'Error al actualizar contador de consumos' });
                  });
                }

                console.log(`Contador de consumos incrementado para modo secuencial ID: ${cellInfo.seq_id}`);
                
                connection.commit(commitErr => {
                  if (commitErr) {
                    return connection.rollback(() => {
                      console.error('Error al confirmar transacción:', commitErr);
                      res.status(500).json({ error: 'Error al procesar el dispensado' });
                    });
                  }

                  res.json({ 
                    message: 'Dispensado registrado correctamente',
                    history_id: historyResult.insertId,
                    mode_type: 'sequential',
                    consumption_updated: true,
                    cell_cleared: true,
                    registered_date: formattedDate
                  });
                });
              });
            } else {
              connection.commit(commitErr => {
                if (commitErr) {
                  return connection.rollback(() => {
                    console.error('Error al confirmar transacción:', commitErr);
                    res.status(500).json({ error: 'Error al procesar el dispensado' });
                  });
                }

                const modeType = cellInfo.single_id ? 'single' : 
                               cellInfo.basic_id ? 'basic' : 'none';

                res.json({ 
                  message: 'Dispensado registrado correctamente',
                  history_id: historyResult.insertId,
                  mode_type: modeType,
                  consumption_updated: false,
                  cell_cleared: true,
                  registered_date: formattedDate
                });
              });
            }
          });
        });
      });
    });
  });
});

app.post('/update_medicine_date', authMiddleware, (req, res) => {
  const { cell_id, current_medicine_date, mac_address } = req.body;
  const userId = req.userId;

  console.log(`Actualizando current_medicine_date para celda ID: ${cell_id}, MAC: ${mac_address}`);
  
  // Convertir timestamp Unix a formato de fecha MySQL
  let formattedDate;
  if (typeof current_medicine_date === 'number') {
    const date = new Date(current_medicine_date * 1000);
    formattedDate = date.toISOString().slice(0, 19).replace('T', ' ');
    console.log(`Timestamp recibido: ${current_medicine_date}, Fecha convertida: ${formattedDate}`);
  } else {
    formattedDate = current_medicine_date;
    console.log(`Fecha recibida como string: ${formattedDate}`);
  }
  
  if (!cell_id || !current_medicine_date || !mac_address) {
    return res.status(400).json({ error: 'Faltan datos requeridos (cell_id, current_medicine_date, mac_address)' });
  }
  
  const checkDispenserQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkDispenserQuery, [mac_address, userId], (err, dispenserResults) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (dispenserResults.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }
    
    const checkCellQuery = 'SELECT * FROM cell WHERE id = ? AND mac_dispenser = ?';
    connection.query(checkCellQuery, [cell_id, mac_address], (cellErr, cellResults) => {
      if (cellErr) {
        console.error('Error al verificar la celda:', cellErr);
        return res.status(500).json({ error: 'Error al verificar la celda' });
      }

      if (cellResults.length === 0) {
        return res.status(404).json({ error: 'Celda no encontrada o no pertenece al dispensador especificado' });
      }
      
      const updateQuery = 'UPDATE cell SET current_medicine_date = ? WHERE id = ? AND mac_dispenser = ?';
      connection.query(updateQuery, [formattedDate, cell_id, mac_address], (updateErr, result) => {
        if (updateErr) {
          console.error('Error al actualizar current_medicine_date:', updateErr);
          return res.status(500).json({ error: 'Error al actualizar la fecha de medicina' });
        }
        
        if (result.affectedRows === 0) {
          return res.status(404).json({ error: 'No se pudo actualizar la celda' });
        }

        console.log(`current_medicine_date actualizado correctamente para celda ID: ${cell_id}`);
        console.log(`Fecha guardada en BD: ${formattedDate}`);
        
        res.json({ 
          message: 'Fecha de medicina actualizada correctamente',
          cell_id: cell_id,
          updated_date: formattedDate,
          original_timestamp: current_medicine_date
        });
      });
    });
  });
});

app.get('/user_dispensers', authMiddleware, (req, res) => {
  const userId = req.userId;
  const query = 'SELECT mac, dispenser_name, context FROM dispenser WHERE user_id = ?';
  
  connection.query(query, [userId], (err, dispenserResults) => {
    if (err) {
      console.error('Error al obtener la información de los dispensadores:', err);
      return res.status(500).json({ error: 'Error al obtener la información de los dispensadores:' });
    }
    
    const macAddresses = dispenserResults.map(row => row.mac);
    const names = dispenserResults.map(row => row.dispenser_name);
    const contexts = dispenserResults.map(row => row.context);
    console.log("Enviando dispensers")
    res.json({ macAddresses: macAddresses,  names: names, contexts: contexts});
  });
});

app.get('/dispenser_cells/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para acceder a este dispensador' });
    }
    const cellsQuery = `
      SELECT id, mac_dispenser, num_cell, current_medicine_date, 
             single_mode_id, sequential_mode_id, basic_mode_id
      FROM cell 
      WHERE mac_dispenser = ?
      ORDER BY num_cell`;

    connection.query(cellsQuery, [macAddress], (cellsErr, cellsResults) => {
      if (cellsErr) {
        console.error('Error al obtener las celdas:', cellsErr);
        return res.status(500).json({ error: 'Error al obtener las celdas' });
      }
      
      res.json(cellsResults);
    });
  });
});

app.get('/single_modes/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para acceder a este dispensador' });
    }

    const query = 'SELECT * FROM single_mode WHERE mac = ?';
    connection.query(query, [macAddress], (err, modes) => {
      if (err) {
        console.error('Error al obtener los modos únicos:', err);
        return res.status(500).json({ error: 'Error al obtener los modos únicos' });
      }
      res.json(modes);
    });
  });
});

app.get('/sequential_modes/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para acceder a este dispensador' });
    }

    const query = 'SELECT * FROM sequential_mode WHERE mac = ?';
    connection.query(query, [macAddress], (err, modes) => {
      if (err) {
        console.error('Error al obtener los modos secuenciales:', err);
        return res.status(500).json({ error: 'Error al obtener los modos secuenciales' });
      }
      res.json(modes);
    });
  });
});

app.get('/basic_modes/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para acceder a este dispensador' });
    }

    const query = 'SELECT * FROM basic_mode WHERE mac = ?';
    connection.query(query, [macAddress], (err, modes) => {
      if (err) {
        console.error('Error al obtener los modos básicos:', err);
        return res.status(500).json({ error: 'Error al obtener los modos básicos' });
      }
      res.json(modes);
    });
  });
});

app.get('/cells_with_modes/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  console.log("Actualizando celdas");

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para acceder a este dispensador' });
    }
    
    const query = `
      SELECT 
        c.id, c.num_cell, c.current_medicine_date,
        sm.id as single_id, sm.medicine_name as single_medicine, sm.dispensing_date,
        sqm.id as seq_id, sqm.medicine_name as seq_medicine, sqm.start_date, 
        sqm.end_date, sqm.period, sqm.limit_times_consumption,
        sqm.affected_periods, sqm.current_times_consumption,
        bm.id as basic_id, bm.medicine_name as basic_medicine,
        bm.morning_start_time, bm.morning_end_time,
        bm.afternoon_start_time, bm.afternoon_end_time,
        bm.night_start_time, bm.night_end_time
      FROM cell c
      LEFT JOIN single_mode sm ON c.single_mode_id = sm.id
      LEFT JOIN sequential_mode sqm ON c.sequential_mode_id = sqm.id
      LEFT JOIN basic_mode bm ON c.basic_mode_id = bm.id
      WHERE c.mac_dispenser = ?
      ORDER BY c.num_cell
    `;

    connection.query(query, [macAddress], (err, results) => {
      if (err) {
        console.error('Error al obtener los datos:', err);
        return res.status(500).json({ error: 'Error al obtener los datos de las celdas' });
      }

      res.json(results);
    });
  });
});

app.get('/history/:mac', authMiddleware, (req, res) => {
    const macAddress = req.params.mac;
    const userId = req.userId;

    const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
    connection.query(checkQuery, [macAddress, userId], (err, results) => {
        if (err) {
            console.error('Error al verificar el dispensador:', err);
            return res.status(500).json({ error: 'Error al verificar el dispensador' });
        }

        if (results.length === 0) {
            return res.status(403).json({ error: 'No tienes permiso para acceder a este dispensador' });
        }
        
        const query = `
            SELECT 
                id,
                mac_dispenser,
                medicine_name,
                consumption_status,
                date_consumption,
                reason
            FROM history 
            WHERE mac_dispenser = ? 
            ORDER BY date_consumption DESC
        `;
        
        connection.query(query, [macAddress], (err, history) => {
            if (err) {
                console.error('Error al obtener el historial:', err);
                return res.status(500).json({ error: 'Error al obtener el historial' });
            }
            
            res.json(history);
        });
    });
});

app.put('/update_single_mode/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { id, medicine_name, dispensing_date } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const updateQuery = 'UPDATE single_mode SET medicine_name = ?, dispensing_date = ? WHERE id = ? AND mac = ?';
    connection.query(updateQuery, [medicine_name, dispensing_date, id, macAddress], (updateErr) => {
      if (updateErr) {
        console.error('Error al actualizar el modo:', updateErr);
        return res.status(500).json({ error: 'Error al actualizar el modo' });
      }
      
      res.json({ message: 'Modo actualizado correctamente' });
    });
  });
});

app.put('/update_sequential_mode/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { 
    id, 
    medicine_name, 
    start_date, 
    end_date, 
    period,
    limit_times_consumption,
    affected_periods,
    current_times_consumption 
  } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const updateQuery = `
      UPDATE sequential_mode 
      SET medicine_name = ?,
          start_date = ?,
          end_date = ?,
          period = ?,
          limit_times_consumption = ?,
          affected_periods = ?,
          current_times_consumption = ?
      WHERE id = ? AND mac = ?
    `;

    connection.query(updateQuery, [
      medicine_name,
      start_date,
      end_date,
      period,
      limit_times_consumption,
      affected_periods,
      current_times_consumption,
      id,
      macAddress
    ], (updateErr) => {
      if (updateErr) {
        console.error('Error al actualizar el modo secuencial:', updateErr);
        return res.status(500).json({ error: 'Error al actualizar el modo secuencial' });
      }
      
      console.log(`Modo secuencial actualizado para dispensador ${macAddress}, ID: ${id}`);
      res.json({ message: 'Modo secuencial actualizado correctamente' });
    });
  });
});

app.put('/update_basic_mode/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { 
    id, 
    medicine_name, 
    morning_start_time,
    morning_end_time,
    afternoon_start_time,
    afternoon_end_time,
    night_start_time,
    night_end_time
  } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const updateQuery = `
      UPDATE basic_mode 
      SET medicine_name = ?,
          morning_start_time = ?,
          morning_end_time = ?,
          afternoon_start_time = ?,
          afternoon_end_time = ?,
          night_start_time = ?,
          night_end_time = ?
      WHERE id = ? AND mac = ?
    `;

    connection.query(updateQuery, [
      medicine_name,
      morning_start_time,
      morning_end_time,
      afternoon_start_time,
      afternoon_end_time,
      night_start_time,
      night_end_time,
      id,
      macAddress
    ], (updateErr) => {
      if (updateErr) {
        console.error('Error al actualizar el modo básico:', updateErr);
        return res.status(500).json({ error: 'Error al actualizar el modo básico' });
      }
      
      console.log(`Modo básico actualizado para dispensador ${macAddress}, ID: ${id}`);
      res.json({ message: 'Modo básico actualizado correctamente' });
    });
  });
});

app.put('/update_cells/:mac', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const userId = req.userId;
  const { cells } = req.body;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    connection.beginTransaction(err => {
      if (err) {
        console.error('Error al iniciar la transacción:', err);
        return res.status(500).json({ error: 'Error al actualizar las celdas' });
      }

      let completedUpdates = 0;
      const totalUpdates = cells.length;

      cells.forEach(cell => {
        const updateQuery = `
          UPDATE cell 
          SET single_mode_id = ?, 
              sequential_mode_id = ?, 
              basic_mode_id = ? 
          WHERE id = ? AND mac_dispenser = ?
        `;
        
        connection.query(
          updateQuery, 
          [
            cell.single_mode_id, 
            cell.sequential_mode_id, 
            cell.basic_mode_id, 
            cell.id, 
            cell.mac_dispenser
          ], 
          (updateErr) => {
            if (updateErr) {
              return connection.rollback(() => {
                console.error('Error al actualizar celda:', updateErr);
                res.status(500).json({ error: 'Error al actualizar las celdas' });
              });
            }

            completedUpdates++;
            
            if (completedUpdates === totalUpdates) {
              connection.commit(commitErr => {
                if (commitErr) {
                  return connection.rollback(() => {
                    console.error('Error al confirmar la transacción:', commitErr);
                    res.status(500).json({ error: 'Error al actualizar las celdas' });
                  });
                }
                res.json({ message: 'Celdas actualizadas correctamente' });
              });
            }
          }
        );
      });
    });
  });
});

app.delete('/delete_single_mode/:mac/:id', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const modeId = req.params.id;
  const userId = req.userId;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const deleteQuery = 'DELETE FROM single_mode WHERE id = ? AND mac = ?';
    connection.query(deleteQuery, [modeId, macAddress], (deleteErr, result) => {
      if (deleteErr) {
        console.error('Error al eliminar el modo:', deleteErr);
        return res.status(500).json({ error: 'Error al eliminar el modo' });
      }
      
      res.json({ message: 'Modo eliminado correctamente' });
    });
  });
});

app.delete('/delete_sequential_mode/:mac/:id', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const modeId = req.params.id;
  const userId = req.userId;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const deleteQuery = 'DELETE FROM sequential_mode WHERE id = ? AND mac = ?';
    connection.query(deleteQuery, [modeId, macAddress], (deleteErr, result) => {
      if (deleteErr) {
        console.error('Error al eliminar el modo secuencial:', deleteErr);
        return res.status(500).json({ error: 'Error al eliminar el modo secuencial' });
      }
      
      res.json({ message: 'Modo secuencial eliminado correctamente' });
    });
  });
});

app.delete('/delete_basic_mode/:mac/:id', authMiddleware, (req, res) => {
  const macAddress = req.params.mac;
  const modeId = req.params.id;
  const userId = req.userId;

  const checkQuery = 'SELECT * FROM dispenser WHERE mac = ? AND user_id = ?';
  connection.query(checkQuery, [macAddress, userId], (err, results) => {
    if (err) {
      console.error('Error al verificar el dispensador:', err);
      return res.status(500).json({ error: 'Error al verificar el dispensador' });
    }
    
    if (results.length === 0) {
      return res.status(403).json({ error: 'No tienes permiso para modificar este dispensador' });
    }

    const deleteQuery = 'DELETE FROM basic_mode WHERE id = ? AND mac = ?';
    connection.query(deleteQuery, [modeId, macAddress], (deleteErr, result) => {
      if (deleteErr) {
        console.error('Error al eliminar el modo básico:', deleteErr);
        return res.status(500).json({ error: 'Error al eliminar el modo básico' });
      }
      
      res.json({ message: 'Modo básico eliminado correctamente' });
    });
  });
});

function validateToken(token) {
  return new Promise((resolve, reject) => {
    const query = `
      SELECT user
      FROM tokens
      WHERE token = ? AND token_exp > ?
    `;
    const currentTimestamp = Math.floor(Date.now() / 1000);

    connection.query(query, [token, currentTimestamp], (err, results) => {
      if (err) {
        console.error('Error al validar el token:', err);
        reject(new Error('Error al validar el token'));
      } else if (results.length === 0) {
        reject(new Error('Token inválido o expirado'));
      } else {
        resolve(results[0].user);
      }
    });
  });
}

function authMiddleware(req, res, next) {
  const token = req.headers['authorization'];
  
  if (!token) {
    return res.status(401).json({ error: 'Token no proporcionado' });
  }

  validateToken(token)
    .then(userId => {
      req.userId = userId;
      next();
    })
    .catch(error => {
      res.status(401).json({ error: error.message });
    });
}

function cleanupExpiredTokens() {
  const currentTimestamp = Math.floor(Date.now() / 1000);
  const query = 'DELETE FROM tokens WHERE token_exp <= ?';
  
  connection.query(query, [currentTimestamp], (err, result) => {
    if (err) {
      console.error('Error al limpiar tokens expirados:', err);
    } else {
      console.log(`Se eliminaron ${result.affectedRows} tokens expirados.`);
    }
  });
}

function decryptPassword(encryptedPassword) {
  const Secret_Key = "1234567890123456";
  const IV = "iughvnbaklsvvkhj";
  encryptedPassword = encryptedPassword.replace(/\r?\n|\r/g, '');

  const decipher = crypto.createDecipheriv('aes-128-cbc', Secret_Key, IV);
  let decrypted = decipher.update(encryptedPassword, 'base64', 'utf8');
  decrypted += decipher.final('utf8');
  return decrypted;
}

cron.schedule('0 * * * *', () => {
  console.log('Ejecutando limpieza de tokens expirados...');
  cleanupExpiredTokens();
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Servidor escuchando en el puerto ${PORT}`);
});