import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1,
    iterations: 100, // one iteration per user
};

export default function () {
    const userId = __ITER + 1;

    const url = `http://localhost:8081/api/v1/users/${userId}/address`;

    const payload = JSON.stringify({
        street: "Main Street",
        zipCode: "12345",
        city: "Sample City",
        flatNo: `A-${userId}`,
        state: "Sample State"
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-USER-ID': String(userId),
            'X-USER-ROLE': 'USER',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    });

    sleep(0.1);
}
