\documentclass[landscape]{article}
\usepackage[landscape]{geometry}   % use landscape format 
\usepackage{graphicx}
\usepackage{array}

% Manually adjust page margins to gain more space for the table
\geometry{a4paper, top=15mm, left=10mm, right=15mm, bottom=30mm,headsep=10mm, footskip=12mm}

% The default alignment point of an image is its bottom edge 
% We use raisebox to move it to the top
\newcommand{\topincludegraphics}[2][]{%
  \raisebox{\dimexpr-\height+\ht\strutbox\relax}{\includegraphics[#1]{#2}}}

\begin{document}

%\section{Appendix}

% The first column is a paragraph (allows for linebreaks with \newline) - all others are c
\begin{tabular}{p{6cm}|c|$columns:{ignored|c}$}
	Title & Colorspace & $columns:{c|$c$}; separator = " & "$ \\\\
	\\hline
	
    $colormap:{cm|$cm.name$\\newline \\scriptsize $cm.desc$ & $cm.colorspace$ & 
    	$cm.images:{img| \\topincludegraphics[height=1cm]{$img$\} }; separator = "& \n"$
    }; separator = "\\\\ \n\\hline \n"$
    
\end{tabular}

\end{document}
