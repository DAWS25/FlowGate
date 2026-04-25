.PHONY: verify clean help
.DEFAULT_GOAL := verify

MVN     := ./mvnw
API_DIR := fg-api

verify:
	cd $(API_DIR) && $(MVN) verify

clean:
	cd $(API_DIR) && $(MVN) clean

help:
	@echo "Targets:"
	@echo "  verify (default)  Compile fg-api and run all tests (mvn verify)"
	@echo "  clean             Remove build artifacts"
