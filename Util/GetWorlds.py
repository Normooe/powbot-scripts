# https://oldschool.runescape.wiki/w/Server?action=edit&section=3
# `pip install requests` and run script to get list of valid members worlds.

import requests
import logging

logging.basicConfig(level=logging.INFO)

link = "https://oldschool.runescape.wiki/w/Server?action=edit&section=3"

response = requests.get(link)
text = response.text.split("\n")

bad_world_identifiers = ["switch", "pvp", "target", "bounty", "skill total", "speedrunning", "high risk"]
get_members = True
print_java_formatted_array = True

if get_members:
	mems = "yes"
else:
	mems = "no"


def format_worlds_string(worlds: list[int]) -> str:
	"""Formats the worlds list into a copy/paste java array."""
	base_string = "public static final int[] WORLD_LIST = "

	formatted_worlds = ""
	for number, world in enumerate(worlds):
		if number % 15 == 0:
			formatted_worlds += "\n"
		else:
			formatted_worlds += f"{world}, "
	return base_string + f"{{{formatted_worlds}}};"


worlds = []
for line in text:
	line = line.lower()
	# Don't include any worlds with identifiers in the bad_world_identifiers list.
	if any(bad_world_identifier in line for bad_world_identifier in bad_world_identifiers):
		continue
	elif f"mems={mems}" in line:
		# line.split() = "worldLine", $worldNumber, $worldLocation, "mems=$yes/no"
		world_number = line.split("|")[1]
		worlds.append(int(world_number))
		logging.info(f"Adding world: {line}")


logging.info("Worlds:\n%s", worlds)
logging.info("Total worlds: %s", len(worlds))
if print_java_formatted_array:
	formatted_string = format_worlds_string(worlds=worlds)
	logging.info("Formatted java string:\n%s", formatted_string)
