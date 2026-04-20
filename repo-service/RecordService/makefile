sqlc:
	sqlc generate
migrateup:
	migrate -path db/migration -database "postgres://root:123456@localhost:5432/root?sslmode=disable" -verbose up	
migratedown:
	migrate -path db/migration -database "postgres://root:123456@localhost:5432/root?sslmode=disable" -verbose down 1

docker:
	docker build -t recordeservice:latest .
contain:
	docker run -d --name record -p 5433:5432 recordeservice:latest
.PHONY: sqlc migrateup migratedown docker contain
