// server.js

const express = require('express');
const helmet = require('helmet');

const app = express();

// Use the Helmet middleware to set security-related headers
app.use(helmet());

// Enable HSTS with a maxAge of 1 year
app.use(
  helmet.hsts({
    maxAge: 31536000, // 1 year in seconds
    includeSubDomains: true,
    preload: true,
  })
);

// Other middleware and routes go here

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
