# Implementation Plan: FaithFlow

> Auto-generated from project-context.json
>
> **Purpose:** Help congregation get an update of church activities, special events like seminar or workshop, news and article
>
> **Backend:** google_sheets

---

## Prerequisites - MUST READ FIRST

**Before starting any phase, review these pattern guidelines:**

1. **Platform-Specific Code:** Use Interface + platformModule injection pattern (NOT expect object)
2. **External Data:** Support multiple input formats, handle missing data gracefully
3. **State Management:** Check existing state before showing input screens

See `CLAUDE.md` (Critical Code Rules section) for detailed patterns.

**For Google Sheets backend:** Load `gsheet-skill` for multi-tab GID discovery and CSV validation patterns.

---

## Overview

This implementation plan outlines the development phases for FaithFlow.
Each phase specifies which skills to load to minimize token usage.

**Total Phases:** 7
**Entities:** 3
**Screens:** 6

---

## Phase 1: Core Structure & Theme

**Load these skills using the Skill tool:**
- `Skill(skill="core-skill")`
- `Skill(skill="theme-skill")`

**Description:** Establish project foundation with proper architecture and theming

**Tasks:**
- [ ] Set up project structure following Compose Multiplatform patterns
- [ ] Generate Material3 theme from ui_design specifications
- [ ] Configure base typography and color schemes
- [ ] Implement dark mode support

---

## Phase 2: Dependency Injection

**Load these skills using the Skill tool:**
- `Skill(skill="koin-di-skill")`

**Description:** Set up Koin DI for clean architecture dependency management

**Tasks:**
- [ ] Configure Koin modules for all layers (data, domain, presentation)
- [ ] Create expect platformModule() in commonMain/di/AppModule.kt
- [ ] Create actual platformModule() in androidMain and iosMain
- [ ] Add allModules() helper that combines platformModule + other modules
- [ ] For platform-specific deps: use Interface + platformModule injection (NOT expect object)
- [ ] Define dependency scopes for ViewModels and repositories

---

## Phase 3: Data Layer

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`
- `Skill(skill="gsheet-skill")`

**Description:** Implement data layer with repository pattern and backend integration

**Tasks:**
- [ ] Create domain models from data_models specifications
- [ ] Implement repository pattern with STATE MANAGEMENT at repository level
- [ ] Create GoogleSheetsService for URL resolution with GID discovery
- [ ] Support both edit URLs and published URLs (pubhtml)
- [ ] Map each entity to its tab name (Assignments, Exams, etc.)
- [ ] Implement multi-format date parsing (yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy)
- [ ] Add CSV response validation (check for HTML error pages)
- [ ] Handle missing tabs gracefully (return empty list, not crash)
- [ ] Implement 3 entity model(s) with CRUD operations

---

## Phase 4: State Management

**Load these skills using the Skill tool:**
- `Skill(skill="state-management-skill")`
- `Skill(skill="coroutine-flow-skill")`

**Description:** Implement shared state management at repository level

**Tasks:**
- [ ] Implement repository-level state management for shared state
- [ ] Set up StateFlow patterns for reactive UI
- [ ] Configure flow collectors and state observers

---

## Phase 5: Navigation & Screens

**Load these skills using the Skill tool:**
- `Skill(skill="ui-skill")`

**Description:** Implement navigation structure and screen composables

**Tasks:**
- [ ] Set up type-safe navigation graph
- [ ] Create screen composables per nav_screens specifications
- [ ] Implement navigation transitions
- [ ] For setup screens: check existing state in ViewModel init (bypass if data exists)
- [ ] Build 6 screen(s) with Compose UI

---

## Phase 6: Feature Orchestration

**Load these skills using the Skill tool:**
- `Skill(skill="feature-orchestration-skill")`

**Description:** Implement ViewModels and use cases following MVI pattern

**Tasks:**
- [ ] Implement THIN ViewModels that observe repository state
- [ ] Create use cases for complex business logic
- [ ] Connect UI → ViewModel → UseCase → Repository → Data flow

---

## Phase 7: Validation & Polish

**Load these skills using the Skill tool:**
- `Skill(skill="validation-skill")`

**Description:** Final validation, error fixing, and polish

**Tasks:**
- [ ] Run build validation
- [ ] Fix any compilation errors
- [ ] Verify all features work correctly
- [ ] Ensure proper error handling throughout
- [ ] Update README.md with actual app info (replace template placeholders with app name, description, features, backend type, and setup instructions from project-context.json)

---

## Implementation Notes

### How to Load Skills

**Use the Skill tool** to load each skill before implementing a phase:

```
Skill(skill="core-skill")
Skill(skill="theme-skill")
```

1. Call `Skill(skill="skill-name")` for each skill listed in the phase
2. The skill content will be loaded with detailed patterns and examples
3. Use the loaded patterns to complete the phase tasks
4. Move to the next phase and repeat

### Key Patterns

- **THIN ViewModels**: ViewModels should only observe repository state, not hold business logic
- **Repository State Management**: Shared state lives in repositories, not ViewModels
- **Type-Safe Navigation**: Use sealed classes for navigation routes
- **Flow Patterns**: Use StateFlow for state, Flow for one-shot operations

### Files Reference

- `docs/project-context.json` - Complete project specifications
- `docs/implementation-plan.md` - This file

---

*Generated by Mismaiti Backend*
