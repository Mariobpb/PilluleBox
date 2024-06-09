const express = require('express');
const mysql = require('mysql');

const app = express();
app.use(express.json());

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
app.post('/auth', (req, res) => {
  const { username, password } = req.body;
  const query = 'SELECT * FROM user WHERE username = ? AND password = ?';
  connection.query(query, [username, password], (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).json({ error: 'Error al buscar el usuario' });
      return;
    }
    if (results.length > 0) {
      res.json({ message: 'Usuario autenticado' });
    } else {
      res.status(401).json({ error: 'Usuario o contraseña incorrectos' });
    }
  });
});


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