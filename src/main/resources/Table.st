\documentclass[landscape]{article}
\usepackage[landscape]{geometry}
\usepackage{graphicx}
\usepackage{array}

\newcolumntype{L}[1]{>{\raggedright\let\newline\\arraybackslash\hspace{0pt}}m{#1}}
\newcolumntype{C}[1]{>{\centering\let\newline\\arraybackslash\hspace{0pt}}m{#1}}
\newcolumntype{R}[1]{>{\raggedleft\let\newline\\arraybackslash\hspace{0pt}}m{#1}}


\geometry{a4paper, top=15mm, left=10mm, right=15mm, bottom=30mm,headsep=10mm, footskip=12mm}

\newcommand{\topincludegraphics}[2][]{%
  \raisebox{\dimexpr-\height+\ht\strutbox\relax}{\includegraphics[#1]{#2}}}

\newcommand{\specialcell}[2][c]{%
  \begin{tabular}[#1]{@{}L{6cm}@{}}#2\end{tabular}}

\begin{document}

%\section{Appendix}
%Here is the text of your introduction.

\begin{tabular}{l|c|$columns:{ignored|c}$}
	Title & Colorspace & $columns:{c|$c$}; separator = " & "$ \\\\
	\\hline
	
    $colormap:{cm|\specialcell{$cm.name$\\\\ \\scriptsize $cm.desc$\} & $cm.colorspace$ & 
    	$cm.images:{img| \\topincludegraphics[height=1cm]{$img$\} }; separator = "& \n"$
    }; separator = "\\\\ \n\\hline \n"$
    
\end{tabular}

\end{document}
