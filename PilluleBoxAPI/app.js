const express = require('express');
const mysql = require('mysql');

const app = express();
app.use(express.json());

const jwt = require('jsonwebtoken');

const sgMail = require('@sendgrid/mail');
sgMail.setApiKey('TU_CLAVE_API_SENDGRID');

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
app.post('/login', (req, res) => {
  const { username_email, password, secretKey } = req.body;
  console.log("\n\nAutenticando: '" + username_email + "' & '" + password + "'" + " | secret key: " + secretKey);
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
      const payload = {
        username_email,
        password
      };
      const options = {
        expiresIn: '1m' // Expiración en una semana
      };
      const token = jwt.sign(payload, secretKey, options);
      const decodedToken = jwt.decode(token);
      const expirationNumber = decodedToken.exp;

      const queryToken = 'UPDATE user SET token = ?, token_exp = ? WHERE id = ?';
      connection.query(queryToken, [token, expirationNumber, id], (err, resultToken) => {
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

app.post('/auth_token', (req, res) => {
  const { token } = req.body;
  console.log("\nValidando token: " + token);

  const query = 'SELECT id, token_exp FROM user WHERE token = ? AND token_exp > ?';
  const currentTimestamp = Math.floor(Date.now() / 1000);
  
  const currentDate = new Date(currentTimestamp * 1000);
  
  connection.query(query, [token, currentTimestamp], (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al verificar el token' });
      return;
    }
    if (results.length > 0) {
      const expirationDate = new Date(results[0].token_exp * 1000);
      
      console.log("Token válido");
      console.log("Fecha actual:", currentDate.toLocaleString());
      console.log("Fecha de expiración:", expirationDate.toLocaleString());
      console.log("Tiempo restante:", Math.floor((expirationDate - currentDate) / 1000)+" segundos");
      
      res.json({ valid: true });
    } else {
      console.log("Token inválido o expirado");
      console.log("Fecha actual:", currentDate.toLocaleString());
      res.status(401).json({ valid: false, error: 'Token inválido o expirado' });
    }
  });
});

app.post('/signup', (req, res) => {
  const { username, email, password } = req.body;
  console.log("Registrando: '" + username + "' & '" + email + "' & '" + password + "'");

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

      const query = 'INSERT INTO user (username, email, password) VALUES (?, ?, ?)';
      connection.query(query, [username, email, password], (err) => {
        if (err) {
          console.error(err);
          console.log(err);
          res.status(500).json({ error: 'Error al registrar el usuario' });
          return;
        }
        res.json({ message: 'Usuario registradoo' });
        console.log(":)");
      });
    });
  });
});

app.post('/emailCode', (req, res) => {
  const { code, email } = req.body;
  const createdAt = new Date();
  const expiresAt = new Date(createdAt.getTime() + (5 * 60 * 1000));

  const query = 'INSERT INTO codes (code, email, created_at, expires_at) VALUES (?, ?, ?, ?)';
  connection.query(query, [code, email, createdAt, expiresAt], (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al registrar el código' });
      return;
    }

    const msg = {
      to: email,
      from: 'mariobpb27@gmail.com',
      subject: 'Código de verificación',
      text: `Tu código de verificación es: ${code}`
    };

    sgMail.send(msg)
      .then(() => {
        console.log('Correo enviado');
        res.json({ message: 'Código registrado y correo enviado' });
      })
      .catch((error) => {
        console.error(error);
        res.status(500).json({ error: 'Error al enviar el correo electrónico' });
      });
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

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Servidor escuchando en el puerto ${PORT}`);
});