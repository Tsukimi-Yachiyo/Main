sqlc:
	sqlc generate
migrateup:
	migrate -path db/migration -database "postgres://root:123456@localhost:5432/root?sslmode=disable" -verbose up	
migratedown:
	migrate -path db/migration -database "postgres://root:123456@localhost:5432/root?sslmode=disable" -verbose down 1

.PHONY: sqlc migrateup migratedown