# https://oldschool.runescape.wiki/w/Server?action=edit&section=3
# Copy paste all the world lines from above source into file that gets opened below and run script.

worlds = []
with open("C:\\Users\\gavin\\Desktop\\worlds.txt") as f:
	for line in f.readlines():
		if "switch" in line:
			# Don't include worlds that change for whatever reason (pvp/leagues/etc)
			pass
		elif "mems=yes" in line:
			world_number = line.split("|")[1]
			worlds.append(int(world_number))


print("Worlds:")
print(worlds)