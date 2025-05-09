package utils

import (
	"bytes"
	"os"
	"text/template"
)

func RenderTemplate(tplPath, outputPath string, data any) error {
	tpl, err := template.ParseFiles(tplPath)
	if err != nil {
		return err
	}
	var buf bytes.Buffer
	if err := tpl.Execute(&buf, data); err != nil {
		return err
	}
	return os.WriteFile(outputPath, buf.Bytes(), 0644)
}
