import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        register_users: {
            executor: 'per-vu-iterations',
            vus: 1000,          // 99 users
            iterations: 1,    // each user registers once
            maxDuration: '2m',
        },
    },
};

export default function () {
    const userIndex = __VU; // 1..99

    const url = 'http://127.0.0.1:8081/api/v1/users/auth/register';

    const payload = JSON.stringify({
        email: `user${userIndex}@example.com`,
        password: 'Password@123',
        firstName: `User${userIndex}`,
        lastName: 'Test',
        role: 'USER',
        phoneNumber: `0900000${String(userIndex).padStart(2, '0')}`,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'user registered': (r) =>
            r.status === 200 || r.status === 201,
    });
}
