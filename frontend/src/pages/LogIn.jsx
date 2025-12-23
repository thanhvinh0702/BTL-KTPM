import React, { useState, useEffect } from "react";
import "../comp_css/Login.css";
import { useNavigate, Link } from "react-router-dom";

// LƯU Ý: Nếu bạn cấu hình base URL trong một file riêng (vd: api/axiosClient.js)
// thì hãy import từ file đó thay vì "axios" mặc định.
// Ví dụ: import axios from "../api/axiosClient";
import loginbg from "../picture/loginbg1.webp";
import api from "../Router/api";


const bg = {
  backgroundImage: `url(${loginbg})`,
  backgroundSize: "cover",
  backgroundRepeat: "no-repeat",
  backgroundPosition: "center center",
  border: "1px solid grey",
  height: "100vh",
};

const formData = {
  email: "", // Backend dùng email
  password: "",
};

const Login = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState(formData);

  useEffect(() => {
    document.title = 'Ecommerse | LogIn';
    return () => {
      document.title = 'Ecommerse App';
    };
  }, []);

  const setHandlerChange = (e) => {
    const val = e.target.value;
    setForm({ ...form, [e.target.name]: val });
  };

  const submitHandler = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post("/api/v1/users/auth/login", {
        email: form.email,
        password: form.password
      });

      console.log("Login Response:", response.data);

      if (response.status === 200 && response.data.token) {
        const token = response.data.token;
        const jwtToken = token.startsWith("Bearer ") ? token : `Bearer ${token}`;

        localStorage.setItem("jwtToken", jwtToken);
        localStorage.setItem("name", form.email);
        localStorage.setItem("userid", response.data.userid)

        alert("Login successfully");
        navigate("/");
      } else {
        alert("Login failed: Invalid response");
      }
    } catch (error) {
      console.error("Login Error:", error);
      if (error.response) {
        // Backend trả về message lỗi thì hiển thị, không thì báo chung
        const msg = error.response.data && error.response.data.message
                    ? error.response.data.message
                    : "Sai Email hoặc Mật khẩu!";
        alert(msg);
      } else {
        alert("Không thể kết nối đến Server.");
      }
    }
  };

  const { email, password } = form;

  return (
    <>
    <div style={bg}>
      <h2 style={{ textAlign: "center", color: "White", margin: "20px" }}>
       WELCOME TO USER LOGIN PAGE
      </h2>
      <div className="loginConatiner" >
        <div className="login-form">
          <h2 style={{ textAlign: "center" }}>LogIn </h2>
          <form onSubmit={submitHandler}>
            <div className="form-group">
              <label htmlFor="email">Email:</label>
              <input
                id="email"
                type="email"
                name="email"
                value={email}
                onChange={setHandlerChange}
                placeholder="Enter your email"
                required
              />
            </div>
            <br />
            <div className="form-group">
              <label>Password:</label>
              <input
                type="password"
                name="password"
                value={password}
                onChange={setHandlerChange}
                required
              />
            </div>
            <div className="form-group">
              <input type="submit" value="Login" />
              <p>
                Don't have an account?{" "}
                <Link to="/register-user">Register here</Link>
              </p>
            </div>
          </form>
        </div>
      </div>
      </div>
    </>
  );
};

export default Login;