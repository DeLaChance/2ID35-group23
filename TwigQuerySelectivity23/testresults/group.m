function group(keys, values, f=1)
	split = {};
	split{max(keys)*f+1} = [];
	for i = 1:rows(keys)
		split{keys(i)*f+1} = [split{keys(i)*f+1}; keys(i), values(i)];
	endfor
	group = [];
	for i = 1:numel(split)
		if columns(split{i}) == 2
			group = [group; (i-1)/f, mean(split{i}(:,2)), std(split{i}(:,2)), prctile(split{i}(:,2),16), prctile(split{i}(:,2),84)];
		endif
	endfor
	plot = errorbar(group(:,1),group(:,2), group(:,4),group(:,5));
	set(plot, 'linewidth', 1)
	set(plot, 'marker','.')
	set(plot, 'markersize', 2)
endfunction
