import React, { useState } from 'react';
import './FeedbackForm.css'; // Import the CSS styles
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import Loader from './Loader';
import Modal from 'react-modal';

function FeedbackForm() {
    const [averageRating, setAverageRating] = useState(0);
    const [commentsRequired, setCommentsRequired] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [modalContent, setModalContent] = useState('');
    const navigate = useNavigate();
    const [responses, setResponses] = useState({
      question1: 0,
      question2: 0,
      question3: 0,
      question4: 0,
      question5: 0,
    });

    const { userID, cartID } = useParams();

    const [userComments, setUserComments] = useState('');
    const [loading, setLoading] = useState(false); 
    const [allQuestionsAnswered, setAllQuestionsAnswered] = useState(false);

    const showAlert = (content) => {
      setModalContent(content);
      setShowModal(true);
    };
  
    const closeModal = () => {
      setShowModal(false);
    };
  
    const handleStarClick = (question, rating) => {
      setResponses({
        ...responses,
        [question]: rating,
      });
  
      if (
        responses.question1 !== 0 &&
        responses.question2 !== 0 &&
        responses.question3 !== 0 &&
        responses.question4 !== 0 &&
        responses.question5 !== 0
      ) {
        setAllQuestionsAnswered(true);
      } else {
        setAllQuestionsAnswered(false);
      }
  
      const newAverageRating =
        (responses.question1 +
          responses.question2 +
          responses.question3 +
          responses.question4 +
          responses.question5 +
          rating) /
        5;
  
      const newCommentsRequired = newAverageRating < 3;
  
      setAverageRating(newAverageRating);
      setCommentsRequired(newCommentsRequired);
    };
  
    const handleSubmit = async () => {
      const isAllQuestionsAnswered =
        responses.question1 !== 0 &&
        responses.question2 !== 0 &&
        responses.question3 !== 0 &&
        responses.question4 !== 0 &&
        responses.question5 !== 0;

      const isCommentsRequired = averageRating < 3;
    
      if (isAllQuestionsAnswered && (!isCommentsRequired || (isCommentsRequired && userComments.trim() !== ''))) {
        console.log('User feedback:', responses);
        console.log('User comments:', userComments);
    
        const feedbackData = {
          cartId: cartID, 
          userId: userID, 
          response: [
            { response: responses.question1 },
            { response: responses.question2 },
            { response: responses.question3 },
            { response: responses.question4 },
            { response: responses.question5 },
          ],
          averageRating: (
            (responses.question1 +
              responses.question2 +
              responses.question3 +
              responses.question4 +
              responses.question5) /
            5
          ).toFixed(1), // Calculate the average rating
          comments: userComments,
        };
    
        try {
          setLoading(true);
          // Send the feedback data to your backend using an API endpoint
          const response = await axios.post('http://localhost:9504/addresponse', feedbackData);

          if (response.status === 201) {
            setLoading(false);
            console.log('Feedback submitted successfully:', response.data);
    
            // Optionally, clear the form fields or navigate to another page
            setResponses({
              question1: 0,
              question2: 0,
              question3: 0,
              question4: 0,
              question5: 0,
            });
            setUserComments('');
            navigate('/FeedBackThank')
          } else {
            setLoading(false);
            // Log an error or handle the response accordingly
            showAlert('Error while submitting feedback. Status:', response.status);
          }
          
        } catch (error) {
          setLoading(false);
          // Handle any errors (e.g., show an error message)
          showAlert('Error while submitting feedback:', error);
        }
      } else {
        showAlert('Please answer all questions before submitting.');
      }
    };
    
    const handleUserCommentsChange = (e) => {
      setUserComments(e.target.value);
    };

  return (
    <div className="feedback-container">
      <div className="feedback-form">
        <h2>Customer Feedback</h2>

        {/* Question 1 */}
        <div className="question">
          <p>1. Were you able to find the exact item you were looking for?
          <span className="mandatory">*</span>
          </p>
          {/* Star ratings */}
          <div className="star-ratings">
            {[1, 2, 3, 4, 5].map((rating) => (
              <span
                key={`question1-rating-${rating}`}
                className={`star ${responses.question1 >= rating ? 'selected' : ''}`}
                onClick={() => handleStarClick('question1', rating)}
              >
                ★
              </span>
            ))}
          </div>
        </div>

        {/* Question 2 */}
        <div className="question">
          <p>2. Is the price of the product reasonable?
          <span className="mandatory">*</span>
          </p>
          {/* Star ratings */}
          <div className="star-ratings">
            {[1, 2, 3, 4, 5].map((rating) => (
              <span
                key={`question2-rating-${rating}`}
                className={`star ${responses.question2 >= rating ? 'selected' : ''}`}
                onClick={() => handleStarClick('question2', rating)}
              >
                ★
              </span>
            ))}
          </div>
        </div>

        {/* Question 3 */}
        <div className="question">
          <p>3. Are you able to quickly compare similar products?
          <span className="mandatory">*</span>
          </p>
          {/* Star ratings */}
          <div className="star-ratings">
            {[1, 2, 3, 4, 5].map((rating) => (
              <span
                key={`question3-rating-${rating}`}
                className={`star ${responses.question3 >= rating ? 'selected' : ''}`}
                onClick={() => handleStarClick('question3', rating)}
              >
                ★
              </span>
            ))}
          </div>
        </div>

        {/* Question 4 */}
        <div className="question">
          <p>4. How is the experience with suggested products if the exact product is not available?
          <span className="mandatory">*</span>
          </p>
          {/* Star ratings */}
          <div className="star-ratings">
            {[1, 2, 3, 4, 5].map((rating) => (
              <span
                key={`question4-rating-${rating}`}
                className={`star ${responses.question4 >= rating ? 'selected' : ''}`}
                onClick={() => handleStarClick('question4', rating)}
              >
                ★
              </span>
            ))}
          </div>
        </div>

        {/* Question 5 */}
        <div className="question">
          <p>5. Your overall shopping experience with us?
          <span className="mandatory">*</span>
          </p>
          {/* Star ratings */}
          <div className="star-ratings">
            {[1, 2, 3, 4, 5].map((rating) => (
              <span
                key={`question5-rating-${rating}`}
                className={`star ${responses.question5 >= rating ? 'selected' : ''}`}
                onClick={() => handleStarClick('question5', rating)}
              >
                ★
              </span>
            ))}
          </div>
        </div>
          {/* User Comments */}
          <div className={`user-comments ${commentsRequired ? 'mandatory' : ''}`}>
          <label htmlFor="comments">
            Comments{commentsRequired ? <span className="mandatory">*</span> : null}:
          </label>
          <div>
            <textarea
              id="comments"
              name="comments"
              value={userComments}
              onChange={handleUserCommentsChange}
              rows="4"
              cols="10"
              className={commentsRequired ? 'mandatory' : ''}
            />
          </div>
        </div>

        {/* Submit button */}
        {loading ? ( 
          <Loader />
        ) : (
        <button className={`text-center ${allQuestionsAnswered ? '' : 'disabled'}`} onClick={handleSubmit}>
          Submit Feedback
        </button>
        )}
      </div>

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

export default FeedbackForm;
