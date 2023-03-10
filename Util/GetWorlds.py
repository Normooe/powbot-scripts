# https://oldschool.runescape.wiki/w/Server?action=edit&section=3
# `pip install requests` and run script to get list of valid members worlds.

import logging
import requests
import sys


logging.basicConfig(level=logging.INFO)

link = "https://oldschool.runescape.wiki/w/Server?action=edit&section=3"

bad_world_identifiers = ["switch", "pvp", "target", "bounty", "skill total", "speedrunning", "high risk"]
get_members = True  # True for only p2p, False for only f2p.
print_java_formatted_array = True
worlds_per_line = 15  # Worlds per line in the formatted java array


def get_page_text(link: str) -> str:
	"""Returns the entire text from the webpage"""
	response = requests.get(link)
	if response.ok:
		return response.text.split("\n")
	else:
		logging.error("Didn't get a valid response from the webpage. Exiting.")
		sys.exit(1)


def format_worlds_string(worlds: list[int]) -> str:
	"""Formats the worlds list into a copy/paste java array."""
	base_string = "public static final int[] WORLD_LIST = "

	formatted_worlds = ""
	for number, world in enumerate(worlds):
		if number % worlds_per_line == 0:
			formatted_worlds += "\n"
		else:
			formatted_worlds += f"{world}, "
	return base_string + f"{{{formatted_worlds}}};"


def check_line(line: str) -> bool:
	"""Checks that the world should be added to the list."""
	# Don't include any worlds with identifiers in the bad_world_identifiers list.
	if "worldline" not in line:
		return False
	if any(bad_world_identifier in line for bad_world_identifier in bad_world_identifiers):
		return False
	return (get_members and "mems=yes" in line) or (not get_members and "mems=no" in line)


worlds = []
for line in get_page_text(link=link):
	line = line.lower()
	if check_line(line):
		# line.split() = "worldLine", $worldNumber, $worldLocation, "mems=$yes/no"
		world_number = line.split("|")[1]
		# Add world to list
		worlds.append(int(world_number))
		logging.info(f"Adding world: {line}")


logging.info("Worlds:\n%s", worlds)
logging.info("Total worlds: %s", len(worlds))
if print_java_formatted_array:
	formatted_string = format_worlds_string(worlds=worlds)
	logging.info("Formatted java string:\n%s", formatted_string)
