import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 500 },
    { duration: '1m', target: 1000 },
    { duration: '30s', target: 2000 },
  ],
};

export default function () {
  const responses = http.batch([
    ['GET', 'http://localhost:8082/api/v1/products'],
    ['GET', 'http://localhost:8081/api/v1/users/auth/ping'],
  ]);

  const authRes = responses[1];

  check(authRes, {
    'Auth service sống': (r) => r.status === 200,
    'Auth < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(1);
}
