import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import api from '../Router/api'
import "../comp_css/SingleProduct.css";

const SingleProduct = () => {
  const navigate = useNavigate();
  const { productId } = useParams();
  const [product, setProduct] = useState({});
  const userid = localStorage.getItem("userid");

  useEffect(() => {
    api
      .get(`/api/v1/products/${productId}`)
      .then((response) => {
        setProduct(response.data);
      })
      .catch((error) => {
        console.error("Error fetching data from the API: ", error);
      });
  }, [productId]);

  const addProductToCart = (productid) => {
    // Lấy cartId từ localStorage (nếu có)
    const cartId = localStorage.getItem("cartid");

    // Tạo request body theo format AddToCartRequest
    const requestBody = {
      cartId: cartId || null, // Gửi null nếu chưa có cart
      userId: userid,
      productId: productid,
      quantity: 1
    };

    api
        .post("/cart/add-product", requestBody)
        .then((response) => {
          // Lưu cartId vào localStorage nếu BE trả về
          if (response.data.cartId) {
            localStorage.setItem("cartid", response.data.cartId);
          }
          alert("Product added to Cart successfully!");
        })
        .catch((error) => {
          if (error.response && error.response.data) {
            // Hiển thị message lỗi từ BE
            alert(error.response.data.message || "Error adding product to cart");
          } else {
            alert("Error adding product. Please try again later.");
            console.error("Error adding to cart:", error);
          }
        });
  }
  return (
    <>
    <h1 style={{color:"green",textAlign:"center",margin:"20px"}}>Single Product </h1>
    <div className="product-container">
     
      <div className="product-details">
        <div className="product_image">
          <img src={product.imageUrl} alt={product.name} />
        </div>

        <div className="product_details">
          <h2>{product.name}</h2>
          <p>Category: {product.category}</p>
          <p>Description: {product.description}</p>
          <p>Price: ₹ {product.price}</p>

          <div>
            <button
              onClick={() => {
                addProductToCart(productId);
              }}
            >
              Add to Cart
            </button>
          </div>
        </div>
      </div>

      <div className="counter-box">
        <div>
          <button
            onClick={() => {
              navigate("/user/cart");
            }}
          >
            Move To Cart
          </button>
        </div>
      </div>
    </div>
    </>
  );
};

export default SingleProduct;
