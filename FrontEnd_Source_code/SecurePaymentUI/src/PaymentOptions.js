// src/components/PaymentOptions.js

import React, { useState } from "react";
import './PaymentOptions.css';

const PaymentOptions = () => {
  const [showCardInput, setShowCardInput] = useState(false);
  const [cardNumber, setCardNumber] = useState("");

  const handleCardClick = () => {
    setShowCardInput(!showCardInput);
  };

  const handleCardNumberChange = (e) => {
    setCardNumber(e.target.value);
  };

  return (
    <div>
      <div className="payment-option" onClick={handleCardClick}>
        <span>Card Payment</span>
        <span className={showCardInput ? "icon-minus" : "icon-plus"}></span>
      </div>
      {showCardInput && (
        <div>
          <input
            type="text"
            placeholder="Enter Card Number"
            value={cardNumber}
            onChange={handleCardNumberChange}
          />
        </div>
      )}

      <div className="payment-option">
        <span>Wallet Payment</span>
        <span className="icon-plus"></span>
      </div>

      <div className="payment-option">
        <span>Cash on Delivery</span>
        <span className="icon-plus"></span>
      </div>
    </div>
  );
};

export default PaymentOptions;
