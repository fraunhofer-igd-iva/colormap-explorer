\documentclass{article}
\usepackage{graphicx}
\usepackage{geometry}
\usepackage{array}
\usepackage{rotating}
\usepackage[table]{xcolor}% http://ctan.org/pkg/xcolor
\usepackage{pdflscape} % adds PDF support to the landscape environment of package lscape, by setting the PDF/Rotate page attribute. 

% Manually adjust page margins to gain more space for the table
\geometry{a4paper, top=15mm, left=10mm, right=15mm, bottom=25mm,headsep=10mm, footskip=12mm}

% The default alignment point of an image is its bottom edge 
% We use raisebox to move it to the top
\newcommand{\topincludegraphics}[2][]{%
  \raisebox{\dimexpr-\height+\ht\strutbox\relax}{\includegraphics[#1]{#2}}}


\begin{document}

$quality:{color| 
\definecolor{$color.name$\}{rgb\}{$color.r$, $color.g$, $color.b$\}
}$

$malus:{color| 
\definecolor{$color.name$\}{rgb\}{$color.r$, $color.g$, $color.b$\}
}$

{
% Centering the content of a table cell is a real pain
% We therefore use \raisebox to do the centering manually for every single cell

\renewcommand{\tabcolsep}{0.0mm}
\begin{tabular}{p{5cm}|c|c|$metrics:{ignored|p{1.1cm\}}; separator = "|"$}
	
	Title & Space & Image & $metrics:{metric|\begin{sideways\} \parbox{5cm\}{$metric.name$ \\newline \\scriptsize $metric.description$\}\end{sideways\}}; separator = " &\n"$ \\\\
	\hline
	
	$colormaps:{cm|$cm.name$ & $cm.colorspace$ & \topincludegraphics[height=9mm]{$cm.imagePath$\} & 
    	$metrics:{metric|\cellcolor{$cm.colors.(metric)$\}\raisebox{-3mm\}{$cm.values.(metric)$\}}; separator = "& \n"$
    }; separator = "\\\\ \n\\hline \n"$
	
	 
\end{tabular}
}

\end{document}

