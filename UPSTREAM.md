# Syncing with upstream

This repository is a personal fork of [NoopApp/noop](https://github.com/NoopApp/noop).

## Remotes

```bash
git remote -v
# origin    https://github.com/nandish-jha/noop-app.git
# upstream  https://github.com/NoopApp/noop.git
```

## Pull a new upstream release

```bash
git fetch upstream
git checkout main
git merge upstream/main
# resolve conflicts if any, then:
git push origin main
```

## Prefer a clean merge commit

```bash
git fetch upstream
git checkout main
git merge upstream/main --no-ff -m "Merge upstream NoopApp/noop"
git push origin main
```

## Notes

- **License:** PolyForm Noncommercial 1.0.0 — see [LICENSE](LICENSE) and [ATTRIBUTION.md](ATTRIBUTION.md).
- **Releases:** Until you publish your own GitHub releases, prebuilt binaries remain on [upstream releases](https://github.com/NoopApp/noop/releases).
- **Donations:** Support the original maintainer via the [upstream donations page](https://github.com/NoopApp/noop/wiki/Donations).
