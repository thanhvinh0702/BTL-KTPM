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
            iterations: 1,    // run the default function once per VU
            maxDuration: '5m', // safety timeout
        },
    },
};

const ITEMS_PER_USER = 50; // change to 10, etc.

export default function () {
    const userId = __VU;

    for (let i = 0; i < ITEMS_PER_USER; i++) {

        // random product between 1 and 100 (inclusive)
        const productId = Math.floor(Math.random() * (100 - 1 + 1)) + 1;

        const url = 'http://localhost:8083/cart/add-product';

        const payload = JSON.stringify({
            userId: userId,
            productId: productId,
            quantity: 1,
        });

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'X-USER-ID': String(userId),
                'X-USER-ROLE': 'USER',
            },
        };

        const res = http.post(url, payload, params);

        placeOrderDuration.add(res.timings.duration);

        check(res, {
            'added to cart': (r) =>
                r.status === 200 || r.status === 201,
        });

        sleep(0.02); // small delay between items
    }
}
