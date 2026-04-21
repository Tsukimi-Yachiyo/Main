package db

import (
	"context"
	"fmt"
	"strings"
)

func (q *Queries) CreateScore(ctx context.Context, tableName string, id int32, score int32) error {
	query := fmt.Sprintf(`
	insert into "%s" (id, score) values ($1, $2)
	`, tableName)

	_, err := q.db.ExecContext(ctx, query, id, score)
	return err
}

func (q *Queries) GetId(ctx context.Context, tableName string, id int32) (int32, error) {
	//////////////////////////////////检验
	if !IsSafeTable(tableName) {
		return 0, fmt.Errorf("unsafe table")
	}

	query := fmt.Sprintf(`
	select id from "%s" where id = $1 limit 1
	`, tableName)

	row := q.db.QueryRowContext(ctx, query, id)
	err := row.Scan(&id)
	return id, err
}

func (q *Queries) GetAnyField(
	ctx context.Context,
	tableName string,
	id int32,
	field string,
) (interface{}, error) {
	//////////////////////////////////检验
	if !IsSafeTable(tableName) {
		return nil, fmt.Errorf("unsafe table")
	}

	query := fmt.Sprintf(`
	SELECT %s FROM "%s" WHERE id = $1 LIMIT 1
	`, field, tableName)

	// 接收查询结果
	var result interface{}
	err := q.db.QueryRowContext(ctx, query, id).Scan(&result)
	return result, err
}

func (q *Queries) UpdateField(
	ctx context.Context,
	tableName string,
	id int32,
	field string,
	value interface{},
) error {

	query := fmt.Sprintf(`
	UPDATE "%s" SET %s = $1 WHERE id = $2
	`, tableName, field)

	_, err := q.db.ExecContext(ctx, query, value, id)
	return err
}

type UpdataScoreParams struct {
	ID        int32
	Field     string
	Tablename string
	Value     interface{}
}

func (s *Store) CheckFieldAccess(tableName, fieldName string) bool {
	var perm string
	err := s.db.QueryRow(`
		SELECT col_description(
			('"'||$1||'"')::regclass, 
			(SELECT ordinal_position FROM information_schema.columns 
			 WHERE table_name = $1 AND column_name = $2)
		)
	`, tableName, fieldName).Scan(&perm)
	return err != nil || perm != "internal"
}

func (s *Store) InsertData(tableName string, data map[string]interface{}) (int64, error) {

	var cols []string
	var vals []interface{}
	var placeholders []string
	//////////////////////////////////检验
	if !IsSafeTable(tableName) {
		return 0, fmt.Errorf("unsafe table")
	}

	i := 1
	for col, val := range data {
		cols = append(cols, fmt.Sprintf(`"%s"`, col))
		vals = append(vals, val)
		placeholders = append(placeholders, fmt.Sprintf("$%d", i))
		i++
	}
	sql := fmt.Sprintf(`
		INSERT INTO "%s" (%s)
		VALUES (%s)
		RETURNING id
	`, tableName,
		strings.Join(cols, ", "),
		strings.Join(placeholders, ", "))

	var id int64
	err := s.db.QueryRow(sql, vals...).Scan(&id)
	return id, err
}
