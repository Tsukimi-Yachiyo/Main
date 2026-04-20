package db

import (
	"strings"
	"sync"

	"github.com/spf13/viper"
)

// 白名单（支持并发安全）
var (
	safeTableMap = make(map[string]bool)
	mu           sync.RWMutex // 并发安全锁
)

// 初始化：从 viper 加载默认白名单
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

// 校验表是否合法
func IsSafeTable(table string) bool {
	mu.RLock()
	defer mu.RUnlock()
	return safeTableMap[table]
}

// 新增表到白名单（创建表时调用）
func AddTableToWhitelist(table string) {
	if table == "" {
		return
	}
	mu.Lock()
	defer mu.Unlock()
	safeTableMap[table] = true
}