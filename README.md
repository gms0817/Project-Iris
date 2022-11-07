# Project-Iris - v3.1.0
Personal AI Companion
### Features/Fixes added to Iris:
- Changed new user form to ask for City/town instead of zipcode in order to use weather api
- Changed "STT" button to a microphone icon
- Added 'Tools' menu
- Added 'Reddit-Scraper' tool which accepts as input 'subreddit and number of posts'
- (WIP) Adjusted reddit scrape too to request API keys before use
### Queries added to Iris: (0 total in v3.1.0)
- Tell user the weather based on city/town provided in new user form or based on input 
  - (BUG)output needs adjusted for towns with more than one word names
  - (BUG) Some locations are not returning uppercase 
  - (BUG) Need to add exception handling for location input
- (WIP) set an alarm
- (WIP) set a reminder