import React, { useEffect, useState } from 'react';
import './Footer.css'; // Import the CSS styles for the footer

function Footer() {
  // State to store the current year
  const [currentYear, setCurrentYear] = useState('');

  // Function to get the current year
  const getCurrentYear = () => {
    const year = new Date().getFullYear();
    setCurrentYear(year.toString());
  };

  // Use useEffect to run the function when the component mounts
  useEffect(() => {
    getCurrentYear();
  }, []);

  return (
    <footer className="footer">
      <p>&copy; {currentYear} Walmart. All Rights Reserved.</p>
    </footer>
  );
}

export default Footer;
