import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        concurrent_checkout: {
            executor: 'per-vu-iterations',
            vus: 100,          // 100 concurrent users
            iterations: 10,    // each user runs once
            maxDuration: '2m',
        },
    },
};


export default function () {
    const userId = __VU;              // user ID = VU
    const iterationIndex = __ITER;    // global iteration number

    // Random product ID for fetching reviews (or you can loop over all products if you want)
    const productId = Math.floor(Math.random() * 100) + 1; // 1-100

    const url = `http://localhost:8082/api/v1/products/${productId}/reviews`;

    const params = {
        headers: {
            'X-USER-ID': String(userId),
            'X-USER-ROLE': 'USER',
        },
    };

    const res = http.get(url, params);

    check(res, {
        'reviews fetched (200)': (r) => r.status === 200,
        'response not empty': (r) => r.body && r.body.length > 0,
    });

    sleep(0.05);
}
