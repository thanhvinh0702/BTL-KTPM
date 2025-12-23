import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

// Track individual request durations
export const placeOrderDuration = new Trend('place_order_duration_ms');

export const options = {
    scenarios: {
        add_to_cart: {
            executor: 'per-vu-iterations',
            vus: 100,         // 100 users
            iterations: 10,    // run the default function once per VU
            maxDuration: '5m', // safety timeout
        },
    },
};


export default function () {
    const userId = __VU;

    const url = `http://localhost:8083/cart/${userId}`;

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-USER-ID': String(userId),
            'X-USER-ROLE': 'USER',
        },
    };

    // Use GET to fetch cart
    const res = http.get(url, params);

    placeOrderDuration.add(res.timings.duration);

    check(res, {
        'fetched cart': (r) => r.status === 200,
    });

    sleep(0.02);
}
