package db

import (
	"strings"
	"sync"

	"github.com/spf13/viper"
)

var (
	safeTableMap = make(map[string]bool)
	mu           sync.RWMutex // 并发安全锁
)

func InitSafeTables() {
	mu.Lock()
	defer mu.Unlock()

	tableStr := viper.GetString("SAFE_TABLES")
	if tableStr == "" {
		return
	}

	list := strings.Split(tableStr, ",")
	for _, t := range list {
		t = strings.TrimSpace(t)
		if t != "" {
			safeTableMap[t] = true
		}
	}
}


func IsSafeTable(table string) bool {
	mu.RLock()
	defer mu.RUnlock()
	return safeTableMap[table]
}

func AddTableToWhitelist(table string) {
	if table == "" {
		return
	}
	mu.Lock()
	defer mu.Unlock()
	safeTableMap[table] = true
}