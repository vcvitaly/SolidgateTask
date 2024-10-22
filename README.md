## How to run the application
1. Spin up local postgres (tested with 16) and create a db solidgate_task.
2. Set env vars in run configuration for SolidgateTaskApplication if needed.
3. Init users ```INSERT INTO users(id, name, balance) SELECT i, CONCAT('user', i::TEXT), 0 FROM generate_series(1, 1000000) AS s(i);```
4. Run the app.
5. Run the request from the Postman collection (can be found in the repo root).
6. Either run it again to observe conflict or change the idempotency key.
7. Monitor the request status via the corresponding endpoint from the postman collection.

## Implementation description and assumptions
Since there were (in my opinion) as few points not mentioned in the task description, I assumed the following:
- users already exist in the db (which is logical since user creation is normally a separate step)
- running a request might take a while, and a user would rather monitor request status than wait for the request to finish

In case if the same request is run twice, I didn't want to wait for the rows to be unlocked and then do the same work all over again.
Hence, the idempotency key and a separate entity.
With the current implementation, SELECT FOR UPDATE does not make much sense if a single node single thread is run.
However, it's possible to introduce more threads and more instances, the solution should theoretically be scalable even in the current state.

## Points for improvement
1. Actually test the multiple threads and multiple instances, apply appropriate fixes.
2. Add more functional tests since I covered only the happy path.
3. Split request into batches which would allow partially completing the request in case of failure and might be useful in case of even bigger requests.  
Return completed and failed user id ranges (sort ids before persisting the request) alongside the status.  
It would require a separate entity for each batch.
4. Use Kafka since at the moment I kind of implemented "Listen to yourself" pattern, only without a queue. Using Kafka would make response even faster, I assume.
