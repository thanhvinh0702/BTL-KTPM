import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1,
    iterations: 100,
};

export default function () {
    const productNo = __ITER + 1;

    const url = 'http://localhost:8082/api/v1/products';

    const payload = JSON.stringify({
        name: `Product ${productNo}`,
        categoryName: `Category ${((productNo - 1) % 5) + 1}`,
        description: `Description for product ${productNo}`,
        imageUrl: `https://example.com/images/product-${productNo}.png`,
        price: 0,
        quantity: 1000000
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-USER-ID': '1',
            'X-USER-ROLE': 'USER' // keep this if your security filter requires it
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'product created (200 or 201)': (r) => r.status === 200 || r.status === 201,
    });

    sleep(0.05);
}
