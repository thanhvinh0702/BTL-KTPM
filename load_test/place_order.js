import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

// Track individual request durations
export const placeOrderDuration = new Trend('place_order_duration_ms');

// Concurrency test
export const options = {
    scenarios: {
        concurrent_checkout: {
            executor: 'per-vu-iterations',
            vus: 100,          // 100 concurrent users
            iterations: 1,    // each user runs once
            maxDuration: '2m',
        },
    },
};

export default function () {
    const userId = __VU; // 1..100

    const url = `http://localhost:8085/ecom/orders/placed/${userId}`;

    const payload = JSON.stringify({});

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-USER-ID': String(userId),
            'X-USER-ROLE': 'USER',
        },
    };

    const res = http.post(url, payload, params);

    // Record response time for histogram
    placeOrderDuration.add(res.timings.duration);

    check(res, {
        'order placed (200 or 201)': (r) =>
            r.status === 200 || r.status === 201,
    });
}
