\documentclass[landscape]{article}
\usepackage[landscape]{geometry}
\usepackage{graphicx}
\geometry{a4paper, top=15mm, left=10mm, right=15mm, bottom=30mm,headsep=10mm, footskip=12mm}

\newcommand{\topincludegraphics}[2][]{%
  \raisebox{\dimexpr-\height+\ht\strutbox\relax}{\includegraphics[#1]{#2}}}


\begin{document}

%\section{Appendix}
%Here is the text of your introduction.

\begin{tabular}{l|$columns:{ignored|c}$}
	Title & $columns:{c|$c$}; separator = " & "$ \\\\
	\\hline
	
    $colormap:{cm|$cm.name$ & 
    	$cm.images:{img| \\topincludegraphics[height=1cm]{$img$\} }; separator = "& \n"$
    }; separator = "\\\\ \n\\hline \n"$
    
\end{tabular}

\end{document}
