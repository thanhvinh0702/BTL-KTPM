import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "../comp_css/Product.css";
import api from "../Router/api";

const Product = () => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [priceOrder, setPriceOrder] = useState("All");
  const [nameSearch, setNameSearch] = useState("");
  let userid = localStorage.getItem("userid");

  const filterProducts = (category, priceOrder, nameSearch, data) => {
    let tempProducts = [...data];

    if (category !== "All") {
      tempProducts = tempProducts.filter(
          (product) => product.categoryName === category
      );
    }

    if (priceOrder === "LowToHigh") {
      tempProducts = tempProducts.sort((a, b) => a.price - b.price);
    } else if (priceOrder === "HighToLow") {
      tempProducts = tempProducts.sort((a, b) => b.price - a.price);
    }

    if (nameSearch !== "") {
      const searchQuery = nameSearch.toLowerCase();
      tempProducts = tempProducts.filter((product) =>
          product.name.toLowerCase().includes(searchQuery)
      );
    }

    setFilteredProducts(tempProducts);
  };

  useEffect(() => {
    api
        .get("/api/v1/products")
        .then((response) => {
          const data = response.data || [];
          setProducts(data);
          filterProducts(selectedCategory, priceOrder, nameSearch, data);
        })
        .catch((error) => {
          console.error("Error fetching data from the API: ", error);
        });
  }, [selectedCategory, priceOrder, nameSearch]); // eslint-disable-line react-hooks/exhaustive-deps

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
      <div className="product-page">
        <div className="filter-section">
          <h2>Filter</h2>
          <hr />
          <label>Category</label>
          <select
              value={selectedCategory}
              onChange={(e) => {
                setSelectedCategory(e.target.value);
              }}
          >
            <option value="All">All</option>
            <option value="Vegetable">Vegetable</option>
            <option value="Fruits">Fruits</option>
            <option value="Electronics">Electronic</option>
            <option value="Gadgets">Gadgets</option>
          </select>
          <br />
          <label>Price:</label>
          <div>
            <select
                value={priceOrder}
                onChange={(e) => {
                  setPriceOrder(e.target.value);
                }}
            >
              <option value="All">All</option>
              <option value="LowToHigh">Low to High</option>
              <option value="HighToLow">High To Low</option>
            </select>
          </div>

          <br />
          <div>
            <h4>By Name</h4>
            <input
                type="text"
                placeholder="Search by name"
                value={nameSearch}
                onChange={(e) => setNameSearch(e.target.value)}
            />
          </div>
        </div>

        <div className="product-list">
          {filteredProducts?.length === 0 ? (
              <h1
                  style={{
                    textAlign: "center",
                    margin: "50px",
                    color: "green",
                    width: "800px",
                  }}
              >
                Product Not found ....
              </h1>
          ) : (
              filteredProducts.map((product) => (
                  <div className="product-card" key={product.id}>
                    <div className="product-image1">
                      <img src={product.imageUrl} alt={product.name} />
                    </div>
                    <div className="product-info">
                      <h2>{product.name}</h2>
                      <p>
                        <strong>Category :</strong> {product.categoryName}
                      </p>
                      <p>
                        <strong>Description: </strong>
                        {product.description ? product.description.substring(0, 25) : ""}
                      </p>
                      <h2 className="product-price">Price: ₹ {product.price}</h2>
                      <p>
                        <strong>Rating :</strong>
                        {/* Kiểm tra an toàn cho reviews */}
                        {!product.reviews || product.reviews.length === 0
                            ? "Not Available"
                            : product.reviews[0].rating}
                      </p>

                      <div>
                        <button onClick={() => addProductToCart(product.id)}>
                          Add to Cart
                        </button>
                        <button>
                          <Link
                              to={`/product/${product.id}`}
                              style={{ textDecoration: "none", color: "white" }}
                          >
                            View
                          </Link>
                        </button>
                      </div>
                    </div>
                  </div>
              ))
          )}
        </div>
      </div>
  );
};

export default Product;