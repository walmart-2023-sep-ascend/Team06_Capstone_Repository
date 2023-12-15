import React from 'react';
import { useLocation } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';
import './ThankYouPage.css';

function ThankYouPage() {
  const location = useLocation();
  const orderConfirmationResponse = location.state?.orderConfirmationResponse || {};
  const modeOfPayment = location.state?.modeOfPayment || 'N/A';
  const paymentStatus = location.state?.paymentStatus || 'N/A';

  // Extract additional details from the order confirmation response
  const {
    orderId,
    destinationOfShipping,
    deliverydate,
    phone,
    totalAmount,
  } = orderConfirmationResponse;

  const { cashOnDeliveryInfo } = location.state || {};

  return (
    <div className="thank-you-container">
      <Header />

      <div className="thank-you-page">
        <h2>Thank You for Your Purchase!</h2>
        <p>Your payment was successful, and your order has been confirmed.</p>

        <div className="order-details">
          <h3>Order Details:</h3>
          <p>Order Number: {orderId || 'N/A'}</p>
          <p>Payment Method: {modeOfPayment}</p>
          <p>Payment Status: {paymentStatus}</p>
          <p>Delivery Date: {deliverydate || 'N/A'}</p>
          {modeOfPayment !== 'Digital Wallet' && (
            <>
              <p>Destination of Shipping: {cashOnDeliveryInfo.deliveryAddress || 'N/A'}</p>
              <p>Phone: {cashOnDeliveryInfo.contactNumber || 'N/A'}</p>
            </>
          )}
          <p>Total Amount: {totalAmount || 'N/A'}</p>
        </div>

        <p className="success-message">We appreciate your business.</p>
        <p className="contact-support">
          If you have any questions or concerns, please contact our customer support.
        </p>
      </div>

      <Footer />
    </div>
  );
}

export default ThankYouPage;