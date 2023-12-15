import React from 'react';
import './App.css';
import PaymentPage from './PaymentPage';
import FeedbackForm from './FeedbackForm';
import ThankYouPage from './ThankYouPage';
import Header from './Header';
import Footer from './Footer';
import { Routes, Route } from 'react-router-dom';
import FeedBackThank from './FeedBackThank';
import ReviewForm from './ReviewForm';
import AdminPage from './AdminPage';
import { Helmet } from 'react-helmet';
import SignIn from './SignIn';



function App() {
  return (
    <div className="App">
      <Helmet>
        <title>Wall-E-Cart Online</title>
        <link rel="icon" type="image/png" href="public/logo.png"/>
      </Helmet>
    <Header />
      <div className="payment-container">
      <Routes>
        <Route path="/PaymentPage/:userID/:cartID/:amount" element={<PaymentPage />} />
        <Route path="/ThankYouPage" element={<ThankYouPage />} />
        <Route path="/FeedbackForm/:userID/:cartID" element={<FeedbackForm />} />
        <Route path="/FeedBackThank" element={<FeedBackThank /> } />
        <Route path="/ReviewForm/:userId/:productId" element={<ReviewForm /> } />
        <Route path="/AdminPage" element={<AdminPage />} />
        <Route path="/SignIn" element={<SignIn />} />
      </Routes>
      </div>
    <Footer />
  </div>
    
  );
}

export default App;