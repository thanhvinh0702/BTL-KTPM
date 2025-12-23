import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../Router/api";
import "../comp_css/Cart.css";

const Cart = () => {
  const navigate = useNavigate();
  const [cartData, setCartData] = useState({});
  const [totalAmount, setTotalAmount] = useState(0);
  let cartId = localStorage.getItem("cartid");
  let userId = localStorage.getItem("userid");

  const apiCallOrderPlaced = () => {
    api
      .post(`/ecom/orders/placed/${userId}`)
      .then((response) => {
        alert("Order Placed Succesfuly.....");
        navigate("/user/order-details");
      })
      .catch((error) => {
        console.error("Error fetching data from the API: ", error);
      });
  };

  const orderPlaced = () => {
    apiCallOrderPlaced();
    
  };
  const fetchCartData = () => {
    api
      .get(`/cart/${userId}`)
      .then((response) => {
        setCartData(response.data);
        setTotalAmount(response.data.totalPrice);
      })
      .catch((error) => {
        console.error("Error fetching data from the API: ", error);
      });
  };

  useEffect(() => {
    document.title = "Ecommerse | Cart";
    fetchCartData();
  }, [cartId, totalAmount]);
  const emptyCart = () => {
    api
      .delete(`/ecom/cart/empty-Cart/${cartId}`)
      .then((response) => {
        setTotalAmount(response.data.toalAmout);
        alert("All cart Item remove");
        fetchCartData();
      })
      .catch((error) => {
        alert("Cart is empty");
      });
  };

  const removeProductfromCart = (productid) => {
    api
      .delete(`/cart/remove-product/${cartId}/${productid}`)
      .then((response) => {
        alert("Product removed from cart");
        fetchCartData();
      })
      .catch((error) => {
        alert("Cart is empty");
      });
  };

  const increaseCount = (productid) => {
    api
      .put(`/cart/increase-productQty/${cartId}/${productid}`)
      .then((response) => {
        setTotalAmount(response.data.totalPrice);
        fetchCartData();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const decreaseCount = (productid) => {
    api
      .put(`/cart/decrease-productQty/${cartId}/${productid}`)
      .then((response) => {
        setTotalAmount(response.data.totalPrice);
        fetchCartData();
      })
      .catch((error) => {
        console.log(error);
        alert("Product can be further decrese....");
      });
  };

  return (
    <div className="cart-page">
      {cartData.items?.length > 0 ? (
        <div className="cart-list">
          {cartData.items.map((item) => (
            <div className="cart-card" key={item.productId}>
              <div className="cartproduct-image1">
                <img src={item.productImage} alt={item.productName} />
              </div>
              <div className="cartproduct-info">
                <h2>{item.productName}</h2>
                <h2 className="cartproduct-price">
                  Price: ₹ {item.priceAtAdd}
                </h2>
                <div className="increaseBtn">
                  <button onClick={() => increaseCount(item.productId)}>
                    +
                  </button>
                  <span
                    style={{
                      fontSize: "25px",
                      color: "red",
                      textAlign: "center",
                    }}
                  >
                    {item.quantity}
                  </span>
                  <button onClick={() => decreaseCount(item.productId)}>
                    -
                  </button>
                </div>
                <div>
                  <button
                    onClick={() =>
                      removeProductfromCart(item.productId)
                    }
                  >
                    Remove
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="empty-cart-message">
          <h1>
            Your cart is empty. <Link to="/">Shop Now</Link>
          </h1>
        </div>
      )}

      <div className="cart-details">
        <h2>Total Cart Amount: </h2>
        <h2>${"   " + totalAmount}</h2>
        <div className="counter-box">
          <div>
            <button onClick={orderPlaced}>Order Placed</button>
          </div>
          <div>
            <button
              onClick={() => emptyCart(cartId)}
              style={{ backgroundColor: "red" }}
            >
              Empty Cart
            </button>
          </div>
          <div>
            <button
              onClick={() => {
                navigate("/user/order-details");
              }}
            >
              Order Page
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
