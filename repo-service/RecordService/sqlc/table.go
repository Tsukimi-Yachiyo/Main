package db

import (
	"fmt"
	"strings"
)

// 固定结构：id + score
type ScoreModel struct {
	ID    uint `gorm:"primaryKey;autoIncrement"`
	Score int  `gorm:"type:int;not null"`
}

func (s *Store) CreateTableFromJSON(tableName string, fieldsMap map[string]string) error {
	var fieldParts []string
	permMap := make(map[string]string)

	for name, typ := range fieldsMap {
		parts := strings.SplitN(typ, ",", 2)
		realType := parts[0]
		perm := "api"
		if len(parts) == 2 {
			perm = parts[1]
		}

		fieldParts = append(fieldParts, fmt.Sprintf(`"%s" %s`, name, realType))
		permMap[name] = perm 
	}


	fieldsSQL := strings.Join(fieldParts, ", ")
	query := fmt.Sprintf(`
	CREATE TABLE "%s" (
		id SERIAL PRIMARY KEY
		%s
	);`, tableName, func() string {
		if fieldsSQL != "" {
			return ", " + fieldsSQL
		}
		return ""
	}())

	_, err := s.db.Exec(query)
	if err != nil {
		return err
	}

	for name, perm := range permMap {
		_, err := s.db.Exec(fmt.Sprintf(`COMMENT ON COLUMN "%s"."%s" IS '%s'`, tableName, name, perm))
		if err != nil {
			return err
		}
	}

	AddTableToWhitelist(tableName)
	
	return nil
}