import React, { useState } from 'react';
import './SignIn.css'; // Import the CSS styles for the sign-in page

function SignIn() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleFetchProfile = async () => {
    try {
      // Get the 'Token' cookie
      const tokenCookie = document.cookie
        .split(';')
        .find((cookie) => cookie.trim().startsWith('Token='));
  
      if (tokenCookie) {
        const token = tokenCookie.split('=')[1];
  
        // Make the fetch request to get the profile
        const response = await fetch('http://52.142.30.237:9003/api/test/profile', {
          headers: {
            'Cookie': `Token='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsaW5nYXJham1rMTYxQGdtYWlsLmNvbSIsImlhdCI6MTcwMjA5OTE4NiwiZXhwIjoxNzAyMTg1NTg2fQ.fy1MbMwTk5uMoMBsGh4KqJagH0nlCzhDh4U5FffB9QE'`,
          },
        });
  
        if (response.ok) {
          const profileData = await response.json();
          console.log('Profile Data:', profileData);
        } else {
          console.error('Failed to fetch profile:', response.status);
          // Handle error, display a message, etc.
        }
      } else {
        console.error('Token cookie not found.');
        // Handle error, display a message, etc.
      }
    } catch (error) {
      console.error('Error during profile fetch:', error);
    }
  };

  const handleSignIn = async () => {
    try {
      const response = await fetch('http://52.142.30.237:9003/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: email,
          password: password,
        }),
      });
  
      if (response.ok) {
        const responseData = await response.json();
  
        // Get all cookies
        const allCookies = document.cookie.split(';');
  
        // Find the 'Token' cookie
        let responseToken;
        for (const cookie of allCookies) {
          const [name, value] = cookie.trim().split('=');
          if (name === 'Token') {
            responseToken = value;
            break;
          }
        }
  
        if (responseToken) {
          // Log the token received from the cookie
          console.log('Token from cookie:', responseToken);
  
          // Perform any additional actions, such as redirecting the user
        } else {
          console.error('Sign In failed: Token not found in the cookie');
          // Handle error, display a message, etc.
        }
      } else {
        console.error('Sign In failed:', response.status);
        // Handle error, display a message, etc.
      }
    } catch (error) {
      console.error('Error during Sign In:', error);
    }
  };
  return (
    <div className="signin-container">
      <div className="signin-header">
        <h2>Sign In</h2>
      </div>
      <div className="signin-form">
        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="form-group">
          <button onClick={handleFetchProfile}>Sign In</button>
        </div>
      </div>
    </div>
  );
}

export default SignIn;