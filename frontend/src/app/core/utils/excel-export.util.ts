import ExcelJS from 'exceljs';

export type ExcelMetaLine = {
  label: string;
  value: string;
};

export type StyledExcelExportOptions = {
  filename: string;
  sheetName: string;
  title: string;
  meta: ExcelMetaLine[];
  headers: string[];
  rows: (string | number | null | undefined)[][];
  columnWidths?: number[];
  moneyColumnIndexes?: number[];
  integerColumnIndexes?: number[];
  highlight?: (row: (string | number | null | undefined)[], columnIndex: number) => 'danger' | 'warning' | 'ok' | null;
};

const COLORS = {
  brand: 'FFBD8429',
  brandDark: 'FF7A4712',
  brandSoft: 'FFF4DFB6',
  ink: 'FF241609',
  border: 'FFDEC79E',
  surface: 'FFFFFAF2',
  surfaceAlt: 'FFFBF1E1',
  white: 'FFFFFFFF',
  dangerSoft: 'FFF8DED9',
  dangerText: 'FFA83226',
  warningSoft: 'FFFFF0CC',
  warningText: 'FF946114',
  okSoft: 'FFE5EFDF',
  okText: 'FF496F4A',
  muted: 'FF7F674C',
};

export function timestampForFilename(date = new Date()): string {
  const pad = (value: number) => String(value).padStart(2, '0');
  return `${date.getFullYear()}${pad(date.getMonth() + 1)}${pad(date.getDate())}_${pad(date.getHours())}${pad(date.getMinutes())}`;
}

export function formatExcelGeneratedAt(date = new Date()): string {
  return new Intl.DateTimeFormat('es-PE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}

export async function downloadStyledExcel(options: StyledExcelExportOptions): Promise<void> {
  const workbook = new ExcelJS.Workbook();
  workbook.creator = 'Cafedronel';
  workbook.created = new Date();

  const worksheet = workbook.addWorksheet(options.sheetName.slice(0, 31), {
    views: [{ state: 'frozen', ySplit: options.meta.length + 3, activeCell: 'A1' }],
  });

  const columnCount = options.headers.length;
  const border = {
    top: { style: 'thin', color: { argb: COLORS.border } },
    left: { style: 'thin', color: { argb: COLORS.border } },
    bottom: { style: 'thin', color: { argb: COLORS.border } },
    right: { style: 'thin', color: { argb: COLORS.border } },
  } as const;

  const titleRow = worksheet.addRow([options.title]);
  worksheet.mergeCells(1, 1, 1, columnCount);
  titleRow.height = 30;
  const titleCell = worksheet.getCell(1, 1);
  titleCell.font = { name: 'Calibri', size: 16, bold: true, color: { argb: COLORS.white } };
  titleCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: COLORS.brandDark } };
  titleCell.alignment = { vertical: 'middle', horizontal: 'left', indent: 1 };
  titleCell.border = border;

  for (const line of options.meta) {
    const row = worksheet.addRow([`${line.label}: ${line.value}`]);
    worksheet.mergeCells(row.number, 1, row.number, columnCount);
    const cell = worksheet.getCell(row.number, 1);
    cell.font = { name: 'Calibri', size: 10, color: { argb: COLORS.ink } };
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: COLORS.surface } };
    cell.alignment = { vertical: 'middle', horizontal: 'left', indent: 1 };
    cell.border = border;
    row.height = 20;
  }

  worksheet.addRow([]);

  const headerRowNumber = worksheet.lastRow!.number + 1;
  const headerRow = worksheet.addRow(options.headers);
  headerRow.height = 24;
  headerRow.eachCell((cell) => {
    cell.font = { name: 'Calibri', size: 11, bold: true, color: { argb: COLORS.white } };
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: COLORS.brand } };
    cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
    cell.border = border;
  });

  const moneyColumns = new Set(options.moneyColumnIndexes ?? []);
  const integerColumns = new Set(options.integerColumnIndexes ?? []);

  options.rows.forEach((rowValues, rowIndex) => {
    const row = worksheet.addRow(rowValues);
    row.height = 22;
    const zebra = rowIndex % 2 === 0 ? COLORS.white : COLORS.surfaceAlt;

    row.eachCell({ includeEmpty: true }, (cell, colNumber) => {
      const rawValue = rowValues[colNumber - 1];
      const highlight = options.highlight?.(rowValues, colNumber - 1);
      const highlightStyle = highlightFill(highlight);

      cell.font = {
        name: 'Calibri',
        size: 10,
        color: { argb: highlightStyle?.font ?? COLORS.ink },
        bold: colNumber === 2,
      };
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: highlightStyle?.fill ?? zebra },
      };
      cell.border = border;

      if (moneyColumns.has(colNumber) && typeof rawValue === 'number') {
        cell.numFmt = '"S/ "#,##0.00';
        cell.alignment = { vertical: 'middle', horizontal: 'right' };
        return;
      }

      if (integerColumns.has(colNumber) && typeof rawValue === 'number') {
        cell.numFmt = '#,##0';
        cell.alignment = { vertical: 'middle', horizontal: 'center' };
        return;
      }

      if (typeof rawValue === 'number') {
        cell.alignment = { vertical: 'middle', horizontal: 'right' };
        return;
      }

      cell.alignment = {
        vertical: 'middle',
        horizontal: colNumber <= 2 ? 'left' : 'center',
        wrapText: colNumber === 2,
      };
    });

    for (let col = 1; col <= columnCount; col += 1) {
      if (!row.getCell(col).value && row.getCell(col).value !== 0) {
        const cell = row.getCell(col);
        cell.fill = {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: zebra },
        };
        cell.border = border;
      }
    }
  });

  options.headers.forEach((header, index) => {
    const preset = options.columnWidths?.[index];
    const column = worksheet.getColumn(index + 1);
    if (preset) {
      column.width = preset;
      return;
    }

    const lengths = [
      header.length,
      ...options.rows.map((row) => String(row[index] ?? '').length),
    ];
    column.width = Math.min(42, Math.max(10, Math.max(...lengths) + 3));
  });

  worksheet.autoFilter = {
    from: { row: headerRowNumber, column: 1 },
    to: { row: headerRowNumber, column: columnCount },
  };

  const buffer = await workbook.xlsx.writeBuffer();
  const blob = new Blob([buffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = options.filename.endsWith('.xlsx') ? options.filename : `${options.filename}.xlsx`;
  link.click();
  URL.revokeObjectURL(url);
}

function highlightFill(tone: 'danger' | 'warning' | 'ok' | null | undefined): { fill: string; font?: string } | null {
  if (tone === 'danger') {
    return { fill: COLORS.dangerSoft, font: COLORS.dangerText };
  }
  if (tone === 'warning') {
    return { fill: COLORS.warningSoft, font: COLORS.warningText };
  }
  if (tone === 'ok') {
    return { fill: COLORS.okSoft, font: COLORS.okText };
  }
  return null;
}
