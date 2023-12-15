// ProductDetails.js
import React, { useEffect } from 'react';
import './ProductDetails.css';

const ProductDetails = ({ productDetails }) => {
    console.log('ProductDetails component is rendering.');
    useEffect(() => {
      console.log('Received product details prop:', productDetails);
    }, [productDetails]);
  
    if (!productDetails) {
      return <div>Loading product details...</div>;
    }

  return (
    <div className="product-details-container">
      <img src={productDetails.iconUrl} alt={productDetails.title} className="product-image" />
      <div className="product-info">
        <h2>{productDetails.title}</h2>
        <p className="product-name">{productDetails.productName}</p>
        <p className="short-description">{productDetails.shortDescription}</p>
        <p className="long-description">{productDetails.longDescription}</p>
      </div>
    </div>
  );
};

export default ProductDetails;
