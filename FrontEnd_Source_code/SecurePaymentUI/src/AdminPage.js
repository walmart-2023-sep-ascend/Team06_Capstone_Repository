import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './AdminPage.css';

const AdminPage = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPendingReviews();
  }, []);

  const fetchPendingReviews = async () => {
    try {
      const response = await axios.get('http://localhost:8989/api/approval/');
      const reviewsWithProductDetails = await Promise.all(
        response.data.map(async (review) => {
          const productDetails = await fetchProductDetails(review.productId);
          return { ...review, productDetails };
        })
      );
      setReviews(reviewsWithProductDetails);
    } catch (error) {
      console.error('Error fetching pending reviews:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchProductDetails = async (productId) => {
    try {
      const response = await axios.get(`http://localhost:8989/api/product/reviewSearchId/${productId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching product details for product ID ${productId}:`, error);
      return null;
    }
  };

  const handleApprove = async (productId, userId, email, comment, rating) => {
    try {
      setLoading(true);
      console.log(productId, userId, email, comment, rating);
      const response = await axios.post(
        `http://localhost:8989/api/product/${productId}/comments`,
        {
          user: {
            userId: userId,
            comment: comment,
            rate: rating,
          },
        },
        {
          headers: {
            'user-id-email': email,
            'approval-status': 'approved',
            'Content-Type': 'application/json',
          },
        }
      );

      console.log('Review approved successfully:', response.data);
      const deleteResponse = await axios.delete(
        `http://localhost:8989/api/approval/delete/${productId}/${userId}`
      );
      console.log('Deletion of approval record successful:', deleteResponse.data);

      window.location.reload();
    } catch (error) {
      console.error('Error approving review:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReject = async (productId, userId, email) => {
    try {
      setLoading(true);
      const response = await axios.delete(
        `http://localhost:8989/api/approval/delete/${productId}/${userId}`,
        {
          headers: {
            'user-id-email': email,
            'approval-status': 'reject',
            'Content-Type': 'application/json',
          },
        }
      );

      console.log('Review rejected successfully:', response.data);

      window.location.reload();
    } catch (error) {
      console.error('Error rejecting review:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {loading && (
        <div className="overlay">
          <div className="loader"></div>
        </div>
      )}

      <h1>Pending Review</h1>
      <div className="admin-container">
        {reviews.map((review) => (
          <div key={review.productId} className="review-container">
            <div className="review-details">
              <h3>{review.productDetails && review.productDetails.length > 0 ? review.productDetails[0].title : 'N/A'}</h3>
              {review.productDetails && review.productDetails.length > 0 && (
                <img src={review.productDetails[0].iconUrl} alt="Product Icon" style={{ maxWidth: '30%', maxHeight: '30%' }} />
              )}
              <p>User Email: {review.mail}</p>
            </div>
            <div className="comments-container">
              {review.comments.map((comment, index) => (
                <div key={index} className="comment-container">
                  <p>User ID: {comment.user.userId}</p>
                  <p>Comment: {comment.user.comment}</p>
                  <p>Rating: {comment.user.rate}</p>
                  <div className="button-container">
                    <button
                      className="approve"
                      onClick={() =>
                        handleApprove(
                          review.productId,
                          comment.user.userId,
                          review.mail,
                          comment.user.comment,
                          comment.user.rate
                        )
                      }
                    >
                      {loading ? 'Approving...' : 'Approve'}
                    </button>
                    <button
                      className="reject"
                      onClick={() =>
                        handleReject(review.productId, comment.user.userId, review.mail)
                      }
                    >
                      {loading ? 'Rejecting...' : 'Reject'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminPage;
