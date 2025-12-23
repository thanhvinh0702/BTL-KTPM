import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // Bỏ useNavigation không dùng
import api from "../Router/api";
import "../comp_css/order.css";

const OrderDetails = () => {
    const navigate = useNavigate();
    const userId = localStorage.getItem("userid");
    const [deleted, setDeleted] = useState(false);
    const [allOrder, setAllOrder] = useState([]);


    // Thêm tham số amount vào hàm
    const handleMakePayment = (orderid, amount) => {
        localStorage.setItem("orderid", orderid);

        // Lưu totalAmount vào localStorage
        localStorage.setItem("totalAmount", amount);

        navigate("/user/make-payment");
    };

    const handleProfileSection = (userid) => {
        navigate(`/user/profile/${userid}`);
    };

    const handleDeleteOrder = (orderId) => {
        if (!window.confirm("Are you sure you want to cancel this order?")) return;

        api
            .delete(`/ecom/orders/users/${userId}/${orderId}`) // Đảm bảo đúng đường dẫn API
            .then((response) => {
                alert("Order cancelled successfully"); // Response có thể là string hoặc object
                const updatedAllOrder = allOrder.filter(
                    (order) => order.orderId !== orderId
                );
                setAllOrder(updatedAllOrder);
                setDeleted(true);
            })
            .catch((error) => {
                console.error("Error deleting order: ", error);
                alert("Failed to cancel order");
            });
    };

    useEffect(() => {
        document.title = "Ecommerce | Order details";
        if (!userId) return;

        api
            .get(`/ecom/orders/orders/${userId}`)
            .then((response) => {
                // Kiểm tra nếu response.data là mảng mới sort
                if (Array.isArray(response.data)) {
                    const sortedOrders = response.data.sort((a, b) => {
                        return new Date(b.orderDate) - new Date(a.orderDate);
                    });
                    setAllOrder(sortedOrders);
                }
                setDeleted(false);
            })
            .catch((error) => {
                console.error("Error fetching data: ", error);
            });
    }, [deleted, userId]);

    return (
        <>
            <div className="container">
                <div className="orderContainer">
                    {allOrder.length > 0 ? (
                        allOrder.map((order, index) => (
                            <div key={order.orderId || index} className="order">
                                <div className="odr1">
                                    <h3>Order Number : {index + 1}</h3>
                                    <p>Order ID: {order.orderId}</p>

                                    {/* Tô màu trạng thái cho dễ nhìn */}
                                    <p>
                                        Status:{" "}
                                        <span
                                            style={{
                                                color:
                                                    order.status === "PENDING"
                                                        ? "orange"
                                                        : order.status === "SHIPPED"
                                                            ? "green"
                                                            : "red",
                                                fontWeight: "bold",
                                            }}
                                        >
                      {order.status}
                    </span>
                                    </p>

                                    <p>Order Date: {new Date(order.orderDate).toLocaleString()}</p>

                                    {/* Sửa totalAmount thành orderAmount theo JSON cũ */}
                                    <h3 style={{ color: "green" }}>
                                        Total Amount: ${order.orderAmount || order.totalAmount || 0}
                                    </h3>

                                    {/* LOGIC NÚT CANCEL: Chỉ cho hủy nếu đang PENDING */}
                                    {order.status === "PENDING" && (
                                        <button
                                            style={{ backgroundColor: "red", color: "white" }}
                                            onClick={() => handleDeleteOrder(order.orderId)}
                                        >
                                            Cancel Order
                                        </button>
                                    )}

                                    {/* LOGIC NÚT THANH TOÁN: Chỉ hiện nếu chưa thanh toán và chưa hủy */}
                                    {order.status === "PENDING" ? (
                                        <button
                                            onClick={() => handleMakePayment(order.orderId,order.orderAmount || order.totalAmount || 0 )}
                                            style={{ marginLeft: "10px" }}
                                        >
                                            Make Payment
                                        </button>
                                    ) : order.status === "SHIPPED" ? (
                                        <button
                                            style={{ backgroundColor: "green", marginLeft: "10px" }}
                                            disabled
                                        >
                                            Shipped
                                        </button>
                                    ) : (
                                        <button
                                            style={{ backgroundColor: "grey", marginLeft: "10px" }}
                                            disabled
                                        >
                                            {order.status}
                                        </button>
                                    )}
                                </div>

                                <div className="odr2">
                                    <h3>Order Items</h3>
                                    <ul>
                                        {/* QUAN TRỌNG: Thêm ? để tránh crash trang web */}
                                        {order.orderItem && order.orderItem.length > 0 ? (
                                            order.orderItem.map((item, idx) => (
                                                <li key={item.orderItemId || idx} style={{marginBottom: '5px'}}>
                                                    {/* Fallback: Nếu không có tên thì hiện ID */}
                                                    <b>{item.productName || `Product ID: ${item.productId}`}</b>
                                                    {" - "}
                                                    Quantity: {item.quantity}
                                                    {" - "}
                                                    Price: ${item.price}
                                                </li>
                                            ))
                                        ) : (
                                            <li>No items in this order</li>
                                        )}
                                    </ul>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div
                            style={{
                                color: "green",
                                fontSize: "20px",
                                border: "2px solid grey",
                                height: "50vh",
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                            }}
                        >
                            <h1>No items present</h1>
                        </div>
                    )}
                </div>

                <div className="box">
                    <h3>User Actions</h3>
                    <button onClick={() => handleProfileSection(userId)}>
                        View Profile
                    </button>
                </div>
            </div>
        </>
    );
};

export default OrderDetails;