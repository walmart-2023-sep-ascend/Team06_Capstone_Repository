import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './PaymentPage.css';
import Loader from './Loader';
import { useParams } from 'react-router-dom';
import Modal from 'react-modal';


function PaymentPage() {

  const [showModal, setShowModal] = useState(false);
  const [modalContent, setModalContent] = useState('');

  const { userID, cartID, amount } = useParams();
  const [state, setState] = useState({
    walletAmount: 0,
    totalAmount: 0,
    paymentMethod: 'credit_card',
    paymentInfo: {
      cardNumber: '',
      cardHolder: '',
      expirationDate: '',
      cvv: '',
    },
    cashOnDeliveryInfo: {
      deliveryAddress: '',
      contactNumber: '',
    },
    paymentInProgress: false,
    accordionActive: 0,
    otp: '',
    timer: 60,
    otpSent: false,
    resendDisabled: false,
    generateOtp: true,
    incorrectOtp: false,
    otpVerified: false,
    walletValidationError: '',
    submitPaymentEnabled: false,
  });

  const [cashOnDeliveryInfo, setCashOnDeliveryInfo] = useState({
    deliveryAddress: '',
    contactNumber: '',
  });

  const showAlert = (content) => {
    setModalContent(content);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
  };

  const [loading, setLoading] = useState(false); 
  const navigate = useNavigate();

  const handlePaymentMethodChange = (method) => {
    console.log(method);
    setState((prev) => ({ ...prev, paymentMethod: method }));
    if (method === 'credit_card') {
      showAlert('Credit Card Payment gateway is not available at the moment - Please try other atlernative payment methods');
      
    } else {
      
    }
  };

  const handleInputChange = (e, category, name) => {
    const { value } = e.target;
    setState((prev) => ({
      ...prev,
      [category]: {
        ...prev[category],
        [name]: value,
      },
    }));
  };

  const handleSubmit = async () => {
    setState((prev) => ({ ...prev, paymentInProgress: true }));
      setState((prev) => ({ ...prev, paymentInProgress: false }));
  
      try {
        setLoading(true);
  
        // If payment method is digital_wallet or cash_on_delivery
        if (
          (state.paymentMethod === 'digital_wallet' && state.otpVerified) ||
          state.paymentMethod === 'cash_on_delivery'
        ) {
  
          // Call walletUpdate only if payment method is digital_wallet
          if (state.paymentMethod === 'digital_wallet') {
            const response = await fetch('http://localhost:9500/walletUpdate', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
              },
              body: JSON.stringify({
                userId: userID,
                cartId: cartID,
                totalAmount: amount,
              }),
            });
  
            if (!response.ok) {
              setLoading(false);
              showAlert('Problem with wallet update - Please try again');
              return;
            }
          }
  
          let modeOfPayment, paymentStatus;
          if (state.accordionActive === 2) {
            modeOfPayment = 'Digital Wallet';
            paymentStatus = 'Paid';
          } else if (state.accordionActive === 3) {
            modeOfPayment = 'Cash on Delivery';
            paymentStatus = 'Pending Payment';
          }
  
          const currentDate = new Date();
          currentDate.setDate(currentDate.getDate() + 2);
          const formattedDate = currentDate.toISOString().slice(0, 19).replace('T', ' ');
  
          const orderConfirmationResponse = await fetch('http://localhost:9500/orderConfirmation', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              cartId: cartID,
              userId: userID,
              amount: state.totalAmount,
              modeOfPayment,
              paymentStatus,
              dateOfDelivery: formattedDate,
            }),
          });
  
          if (orderConfirmationResponse.ok) {
            setLoading(false);
            // Redirect to ThankYou page on successful order confirmation
            const thankYouResponse = await orderConfirmationResponse.json();
            navigate('/ThankYouPage', {
              state: {
                orderConfirmationResponse: thankYouResponse,
                modeOfPayment,
                paymentStatus,
                cashOnDeliveryInfo,
              },
            });
          } else {
            setLoading(false);
            // Show a popup to the user with an error message
            showAlert('Problem with order confirmation - Please try again');
          }
        } else {
          setLoading(false);
          showAlert('Invalid payment method');
        }
      } catch (error) {
        setLoading(false);
        showAlert('Error during payment, Please try again after sometimes');
      }
  };
  
  
  const toggleAccordion = async (index) => {
    if (state.accordionActive === index) {
      setState((prev) => ({ ...prev, accordionActive: null }));
    } else {
      setState((prev) => ({ ...prev, accordionActive: index }));

      if (index === 2) {
        handlePaymentMethodChange('digital_wallet');
        await fetchWalletValidation();
      } else if (index === 3) {
        handlePaymentMethodChange('cash_on_delivery');
        await fetchShippingDetails();
      } else if (index === 1) {
        handlePaymentMethodChange('credit_card');
      }
    }
  };

  const handleOtpChange = (e) => {
    const value = e.target.value;
    setState((prev) => ({ ...prev, otp: value }));
  };

  const handleResendOtp = () => {
    console.log('Resending OTP...');
    setState((prev) => ({
      ...prev,
      timer: 60,
      otpSent: true,
      resendDisabled: true,
      generateOtp: true,
      incorrectOtp: false,
    }));
  };

  const handleGenerateOtp = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:9500/authentication', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userID,
        }),
      });

      if (!response.ok) {
        setLoading(false);
        throw new Error(`HTTP error! Status: ${response.status}`);
      } else {
        setLoading(false);
      }

      setState((prev) => ({
        ...prev,
        otpSent: true,
        resendDisabled: true,
        generateOtp: false,
        incorrectOtp: false,
        timer: 30, // Reset the timer to 30 seconds
      }));

      // Start the timer
      const timerInterval = setInterval(() => {
        setState((prev) => ({
          ...prev,
          timer: Math.max(0, prev.timer - 1),
        }));
      }, 1000);

      // Clear the interval when the timer reaches 0
      setTimeout(() => {
        clearInterval(timerInterval);
        setState((prev) => ({
          ...prev,
          resendDisabled: false,
        }));
      }, state.timer * 1000);

    } catch (error) {
      setLoading(false);
      showAlert(`Error generating OTP: ${error.message}`);
    }
  };

  const handleOtpValidation = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:9500/otpValidation', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userID, // Use the userId from the authentication request
          otp: state.otp, // Use the entered OTP
        }),
      });
      
      const result = await response.json();

      if (result.responsecode === '200') {
        setLoading(false);
        // Handle successful OTP validation
        console.log('Successfully Authenticated');
        console.log(state.otp);
        setState((prev) => ({ ...prev, submitPaymentEnabled: true, otpVerified: true, incorrectOtp: false }));
      } else {
        setLoading(false);
        // Handle failed OTP validation
        console.error('OTP validation failed');
        setState((prev) => ({ ...prev, submitPaymentEnabled: false, otpVerified: false, incorrectOtp: true }));
      }
    } catch (error) {
      setLoading(false);
      showAlert(`Error during OTP validation: ${error.message}`);
      setState((prev) => ({ ...prev, submitPaymentEnabled: false, otpVerified: false, incorrectOtp: true }));
    }
  };

  useEffect(() => {
    console.log('Component rendered and mounted.');
    const fetchPayableAmount = async () => {
      try {
        setLoading(true);
        const response = await fetch('http://localhost:9500/payableAmount', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            cartId: cartID,
            userId: userID,
            amount: amount,
          }),
        });

        if (!response.ok) {
          setLoading(false);
          throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.json();

        if (result.walletAmount !== undefined) {
          setLoading(false);
          setState((prev) => ({ ...prev, totalAmount: result.totalAmount }));
        } else {
          setLoading(false);
          showAlert('Wallet amount not found in the API response');
        }
      } catch (error) {
        setLoading(false);
        showAlert(`We encountered an issue while fetching your cart amount. Please try again later.`);
      }
    };

    fetchPayableAmount();
  }, []);

  const fetchWalletValidation = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:9500/walletValidation', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userID,
          totalAmount: state.totalAmount,
        }),
      });

      if (!response.ok) {
        setLoading(false);
        showAlert("It is taking time to load than usual, Please try again or contact our customer service desk");
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const result = await response.json();

      if (result.responsecode === '200') {
        setLoading(false);
        setState((prev) => ({
          ...prev,
          generateOtp: true,
          walletAmount: result.walletAmount,
        }));
      } else if (result.responsecode === '403') {
        setLoading(false);
        setState((prev) => ({
          ...prev,
          generateOtp: false,
          walletValidationError: 'Insufficient Wallet Amount, Please use alternative payment method',
          walletAmount: result.walletAmount,
        }));
      } else {
        setLoading(false);
        showAlert('Unexpected Error:', result.responsecode, 'Please contact cutomer service with this error code');
      }
    } catch (error) {
      setLoading(false);
      showAlert(`We apologize, but there was an issue retrieving your wallet balance. Please try again later.`);
    }
  };

  const fetchShippingDetails = async (onShippingDetailsFetched) => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:9500/shippingDetails', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userID,
          cartId: cartID,
          totalAmount: amount,
        }),
      });

      if (response.ok) {
        setLoading(false);
        const data = await response.json();
        if (data.responsecode === '200') {
          const { destinationOfShipping, phone } = data;
          
          setState((prev) => ({
            ...prev,
            cashOnDeliveryInfo: {
              ...prev.cashOnDeliveryInfo,
              deliveryAddress: destinationOfShipping,
              contactNumber: phone,
            },
            submitPaymentEnabled: true,
            otpVerified: true,
            
          }));
          setCashOnDeliveryInfo({
            deliveryAddress: data.destinationOfShipping,
            contactNumber: data.phone,
          });
          if (typeof onShippingDetailsFetched === 'function') {
            onShippingDetailsFetched(cashOnDeliveryInfo);
          } else {
            console.error('onShippingDetailsFetched is not a function');
          }
        } else {
          setLoading(false);
          setState((prev) => ({ ...prev, submitPaymentEnabled: false }));
          showAlert('Error fetching shipping details:', data.message);
        }
      } else {
        setLoading(false);
        showAlert('Error fetching shipping details:', response.statusText);
      }
    } catch (error) {
      setLoading(false);
      showAlert('Error fetching shipping details:', error);
    }
  };

  useEffect(() => {
    let intervalId;
  
    if (state.timer > 0) {
      intervalId = setInterval(() => {
        setState((prev) => ({
          ...prev,
          timer: prev.timer - 1,
        }));
      }, 1000);
    }
  
    return () => {
      clearInterval(intervalId);
    };
  }, [state.timer]);

  return (
    <div className="payment-container">
      <div className="payment-page">
        <h2 className="payment-title">Secure Payment Page</h2>
        {loading && <Loader />}

        <div className="total-payable-amount">
          <p>Total Payable Amount: $ {state.totalAmount}</p>
        </div>

        <div className="payment-methods">
          <div className={`accordion ${state.accordionActive === 1 ? 'active' : ''}`}>
            <div className="accordion-title" onClick={() => handlePaymentMethodChange('credit_card')}>
              Credit Card
              <div className={`accordion-arrow ${state.accordionActive === 1 ? 'active' : ''}`}></div>
            </div>
            <div className="accordion-content">
              <input
                type="text"
                name="cardNumber"
                placeholder="Card Number"
                value={state.paymentInfo.cardNumber}
                onChange={(e) => handleInputChange(e, 'paymentInfo', 'cardNumber')}
              />
              <input
                type="text"
                name="cardHolder"
                placeholder="Card Holder Name"
                value={state.paymentInfo.cardHolder}
                onChange={(e) => handleInputChange(e, 'paymentInfo', 'cardHolder')}
              />
              <input
                type="text"
                name="expirationDate"
                placeholder="Expiration Date (MM/YY)"
                value={state.paymentInfo.expirationDate}
                onChange={(e) => handleInputChange(e, 'paymentInfo', 'expirationDate')}
              />
              <input
                type="text"
                name="cvv"
                placeholder="CVV"
                value={state.paymentInfo.cvv}
                onChange={(e) => handleInputChange(e, 'paymentInfo', 'cvv')}
              />
            </div>
          </div>

          <div className={`accordion ${state.accordionActive === 2 ? 'active' : ''}`}>
            <div
              className="accordion-title"
              onClick={() => {
                toggleAccordion(2);
                handlePaymentMethodChange('digital_wallet');
              }}
            >
              Digital Wallet
              <div className={`accordion-arrow ${state.accordionActive === 2 ? 'active' : ''}`}></div>
            </div>
            <div className="accordion-content">
            <div className="digital-wallet-info">
                {state.walletValidationError && <p style={{ color: 'red' }}>{state.walletValidationError}</p>}
                <p>Your wallet balance: $ {state.walletAmount}</p>

                {state.otpSent ? (
                  state.otpVerified ? (
                    <div>
                      <p>Successfully Authenticated</p>
                      <p>Submit Payment to confirm your order</p>
                    </div>
                  ) : (
                    <>
                      <input
                        type="text"
                        name="otp"
                        placeholder="Enter OTP"
                        value={state.otp}
                        onChange={handleOtpChange}
                        className="otp-input"
                      />
                      <button onClick={handleOtpValidation} className="otp-validation-button">
                        Verify OTP
                      </button>
                      {state.incorrectOtp ? (
                        <>
                          <p style={{ color: 'red' }}>Incorrect OTP</p>
                          {state.timer > 0 ? (
                            <p>Resend OTP in {state.timer} seconds</p>
                          ) : (
                            <button onClick={handleResendOtp} disabled={state.resendDisabled} className="otp-resend-button">
                              Resend OTP
                            </button>
                          )}
                        </>
                      ) : (
                        state.timer > 0 ? (
                          <p>Resend OTP in {state.timer} seconds</p>
                        ) : (
                          <button onClick={handleGenerateOtp} className="otp-resend-button">
                            Generate OTP
                          </button>
                        )
                      )}
                    </>
                  )
                ) : (
                  <>
                    {state.generateOtp && (
                      <button onClick={handleGenerateOtp} className="otp-resend-button">
                        Generate OTP
                      </button>
                    )}
                  </>
                )}
              </div>

  </div>
          </div>

          <div className={`accordion ${state.accordionActive === 3 ? 'active' : ''}`}>
            <div
              className="accordion-title"
              onClick={() => toggleAccordion(3)}
            >
              Cash on Delivery
              <div className={`accordion-arrow ${state.accordionActive === 3 ? 'active' : ''}`}></div>
            </div>
            <div className="accordion-content">
              <input
                type="text"
                name="deliveryAddress"
                placeholder="Delivery Address"
                value={state.cashOnDeliveryInfo.deliveryAddress}
                onChange={(e) => handleInputChange(e, 'cashOnDeliveryInfo', 'deliveryAddress')}
                disabled={true} 
              />
              <input
                type="text"
                name="contactNumber"
                placeholder="Contact Number"
                value={state.cashOnDeliveryInfo.contactNumber}
                onChange={(e) => handleInputChange(e, 'cashOnDeliveryInfo', 'contactNumber')}
                disabled={true} 
              />
            </div>
          </div>
        </div>

        <button onClick={handleSubmit} disabled={!state.otpVerified || !state.submitPaymentEnabled}>
          Submit Payment
        </button>
      </div>

      {state.paymentInProgress && (
        <div className="overlay">
          <Loader />
        </div>
      )}

      <Modal
        isOpen={showModal}
        onRequestClose={closeModal}
        contentLabel="Custom Alert"
        className="custom-modal"
        overlayClassName="custom-modal-overlay"
      >
        <div>
          <p>{modalContent}</p>
          <button onClick={closeModal}>Close</button>
        </div>
      </Modal>
    </div>
  );
}

export default PaymentPage;
