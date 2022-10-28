# https://oldschool.runescape.wiki/w/Server?action=edit&section=3
# Copy and paste all the world lines from above source into file that gets opened below and run script.

import requests

link = "https://oldschool.runescape.wiki/w/Server?action=edit&section=3"

response = requests.get(link)
text = response.text.split("\n")

worlds = []
for line in text:
	if "switch" in line:
		# Don't include worlds that change for whatever reason (pvp/leagues/etc)
		pass
	elif "mems=yes" in line:
		world_number = line.split("|")[1]
		worlds.append(int(world_number))


print(f"Worlds:\n {worlds}")
