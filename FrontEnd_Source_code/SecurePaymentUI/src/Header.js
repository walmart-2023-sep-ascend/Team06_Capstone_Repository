// Header.js

import React, { useState } from 'react';
import logo from './logo.png';
import avatar from './user-avatar.png'; 
import './Header.css';

function Header() {
  const [dropdownVisible, setDropdownVisible] = useState(false);
  const [menuVisible, setMenuVisible] = useState(false);

  const handleMouseEnter = () => {
    setDropdownVisible(true);
  };

  const handleMouseLeave = () => {
    setDropdownVisible(false);
  };

  const handleViewProfile = () => {
    console.log('View Profile clicked');
  };

  const handleLogout = () => {
    console.log('Logout clicked');
  };

  const handleToggleMenu = () => {
    setMenuVisible(!menuVisible);
  };

  const handleSearch = (query) => {
    console.log('Search:', query);
  };

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo">
          <img src={logo} alt="Walmart Logo" />
        </div>
        <div className="menu-icon" onClick={handleToggleMenu}>
          ☰
        </div>
        <div className={`navigation-menu ${menuVisible ? 'active' : ''}`}>
          <ul>
            <li onClick={handleToggleMenu}>Home</li>
            <li onClick={handleToggleMenu}>Products</li>
          </ul>
        </div>
        <div className="title">
          <h3>Wall-E-Cart Online Shopping</h3>
        </div>
        <div className="search-bar">
          <input type="text" placeholder="Search" onChange={(e) => handleSearch(e.target.value)} />
        </div>
        
        <div className="user-avatar" onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
          <img src={avatar} alt="User Avatar" className="avatar" />
          {dropdownVisible && (
            <div className="dropdown-content">
              <button onClick={handleViewProfile}>View Profile</button>
              <button onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
            
      </div>
    </header>
  );
}

export default Header;
