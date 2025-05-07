package utils

import (
	"fmt"
	"strings"
)

func FormatPortList(ports []int) string {
	var b strings.Builder
	b.WriteString("[")
	for i, p := range ports {
		if i > 0 {
			b.WriteString(", ")
		}
		b.WriteString(fmt.Sprintf("%d", p))
	}
	b.WriteString("]")
	return b.String()
}
