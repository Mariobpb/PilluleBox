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

  // Imprimir contraseñas desencriptadas
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
          console.log("Login exitoso y dispenser actualizado");
        });
    }

      // Generación de token
      const payload = {
        username_email,
        password
      };
      const options = {
        expiresIn: '1m' // Expiración "w" : semanas, "m" : minutos
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
          console.log("Login exitoso y dispenser actualizado");
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
/*
app.get('/users', (req, res) => {
  const query = 'SELECT * FROM user';
  connection.query(query, (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al obtener los datos' });
      return;
    }
    res.json(results);
  });
});
*/
/*
app.post('/registros', (req, res) => {
  const { led, fecha, hora } = req.body;
  const query = 'INSERT INTO Registros (led, fecha, hora) VALUES (?, ?, ?)';
  connection.query(query, [led, fecha, hora], (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al agregar un nuevo dato' });
      return;
    }
    res.json({ id: result.insertId });
  });
});
 
app.delete('/registros/:id', (req, res) => {
  const id = req.params.id;
  const query = 'DELETE FROM Registros WHERE id = ?';
  connection.query(query, [id], (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al eliminar el registro' });
      return;
    }
    if (result.affectedRows === 0) {
      res.status(404).json({ error: 'Registro no encontrado' });
      return;
    }
    res.json({ message: 'Registro eliminado correctamente' });
  });
});
 
app.patch('/registros/:id', (req, res) => {
  const id = req.params.id;
  const { led, fecha, hora } = req.body;
  const query = 'UPDATE Registros SET led = ?, fecha = ?, hora = ? WHERE id = ?';
  connection.query(query, [led, fecha, hora, id], (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al actualizar el registro' });
      return;
    }
    if (result.affectedRows === 0) {
      res.status(404).json({ error: 'Registro no encontrado' });
      return;
    }
    res.json({ message: 'Registro actualizado correctamente' });
  });
});
*/

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