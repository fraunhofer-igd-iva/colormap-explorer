\documentclass[landscape]{article}
\usepackage{graphicx}
\usepackage{geometry}
\usepackage{array}
\usepackage{rotating}
\usepackage[table]{xcolor}% http://ctan.org/pkg/xcolor

% Manually adjust page margins to gain more space for the table
\geometry{a4paper, top=15mm, left=10mm, right=15mm, bottom=25mm,headsep=10mm, footskip=12mm}

\begin{document}

$quality:{color| 
\definecolor{$color.name$\}{rgb\}{$color.r$, $color.g$, $color.b$\}
}$

$malus:{color| 
\definecolor{$color.name$\}{rgb\}{$color.r$, $color.g$, $color.b$\}
}$

{
\renewcommand{\tabcolsep}{0.0mm}
\begin{tabular}{p{5cm}|$colormaps:{ignored|c}; separator = "|"$}
	
	Title & $colormaps:{cm|\begin{turn\}{90\} $cm.name$ \end{turn\}}; separator = " & "$  \\\\ \\hline
	
	Image & $colormaps:{cm|\includegraphics[height=8mm]{$cm.imagePath$\}}; separator = " & "$  \\\\ \\hline

	ColorSpace & $colormaps:{cm|\\scriptsize $cm.colorspace$}; separator = " & "$  \\\\ \\hline
	
	$metrics:{metric|
	 $metric.name$ \\newline
     \\scriptsize $metric.description$ &  
	 $colormaps:{cm|\cellcolor{$cm.colors.(metric)$\} $cm.values.(metric)$}; separator = " &\n"$  \\\\
	}; separator = "\\hline\n" $
\end{tabular}
}

\end{document}

