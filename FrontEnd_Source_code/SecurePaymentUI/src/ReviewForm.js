import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ReviewForm.css';
import { useParams, useNavigate } from 'react-router-dom';

const ReviewForm = () => {
  const navigate = useNavigate();
  const { userId, productId } = useParams();
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [email, setEmail] = useState('');
  const [productDetails, setProductDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [emailError, setEmailError] = useState('');
  const [product, setProduct] = useState('');

  useEffect(() => {
    fetchProductDetails();
  }, [productId]);

  const fetchProductDetails = async () => {
    try {
      setProduct("Loading product details...");
      const response = await axios.get(`http://localhost:8989/api/product/reviewSearchId/${productId}`);
      console.log('Product details response:', response.data);

      if (Array.isArray(response.data) && response.data.length > 0) {
        setProductDetails(response.data[0]);
      } else {
        setProduct("No Products Found . . .");
        setLoading(false);
        console.error('Invalid product details response:', response.data);
      }
    } catch (error) {
        setProduct("No Products Found . . .");
        setLoading(false);
      console.error('Error fetching product details:', error);
    }
  };

  const handleRatingChange = (selectedRating) => {
    setRating(selectedRating);
  };

  const handleCommentChange = (event) => {
    setComment(event.target.value);
  };

  const handleEmailChange = (event) => {
    const inputEmail = event.target.value;
    setEmail(inputEmail);

    // Email validation
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    setEmailError(emailPattern.test(inputEmail) ? '' : '         Invalid email address');
  };

  const handleSubmit = async () => {
    if (emailError) {
      return;
    }

    setLoading(true);
    const requestBody = {
      comments: [
        {
          user: {
            userId: userId,
            comment: comment,
            rate: rating,
          },
        },
      ],
    };

    try {
      const response = await axios.post(`http://localhost:8989/api/approval/${productId}/comment`, requestBody, {
        headers: {
          'Content-Type': 'application/json',
          'user-id-email': email,
          'Authorization': 'Token=',
        },
      });

      console.log('API Response:', response.data);
      console.log('Review submitted:', { rating, comment, email });
      setRating(0);
      setComment('');
      navigate('/FeedBackThank');
    } catch (error) {
      console.error('Error submitting review:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="review-form-container">
      <div className="review-form">
        {productDetails ? (
          <div className="product-details-container">
            <img src={productDetails.iconUrl} alt={productDetails.title} className="product-image" />
            <div className="product-info">
              <h2>{productDetails.title}</h2>
              <p className="product-name">{productDetails.productName}</p>
              <p className="short-description">{productDetails.shortDescription}</p>
              <p className="long-description">{productDetails.longDescription}</p>
            </div>
          </div>
        ) : (
          <div>{product}</div>
        )}

        <h1>Add Review</h1>
        <div className="star-ratings">
          {[1, 2, 3, 4, 5].map((index) => (
            <span
              key={index}
              className={`star ${rating >= index ? 'selected' : ''}`}
              onClick={() => handleRatingChange(index)}
            >
              ★
            </span>
          ))}
        </div>

        <br />
        <label className="review-form-label">
          Email <br />
          <div className="email-input-container">
            <input
              type="email"
              className={`review-form-input ${emailError ? 'error' : ''}`}
              value={email}
              onChange={handleEmailChange}
            />
            
          </div>
          <div>{emailError && <span className="error-message">{emailError}</span>}</div>
        </label>

        <br />
        <label className="review-form-label">
          Comment <br />
          <textarea className="review-form-textarea" value={comment} onChange={handleCommentChange} />
        </label>
        <br />
        <button className="submit-button" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Submitting...' : 'Submit Review'}
        </button>
      </div>

      {loading && (
        <div className="overlay">
          <div className="loader"></div>
        </div>
      )}
    </div>
  );
};

export default ReviewForm;
