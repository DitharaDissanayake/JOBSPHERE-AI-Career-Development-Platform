const API = 'http://localhost:8080/api';

const $ = id => document.getElementById(id);


/* =========================================================
   USER
========================================================= */

function user() {

    try {

        return JSON.parse(
            localStorage.getItem('jobsphereUser') || 'null'
        );

    } catch {

        return null;

    }

}


/* =========================================================
   TOKEN
========================================================= */

function token() {

    return localStorage.getItem('jobsphereToken') || '';

}


/* =========================================================
   AUTHORIZATION
========================================================= */

function auth() {

    return {
        'Authorization': 'Bearer ' + token()
    };

}


/* =========================================================
   REQUIRE LOGIN
========================================================= */

function requireUser() {

    const u = user();

    if (!u) {

        location.href = 'login.html';

        throw new Error('Login required');

    }

    return u;

}


/* =========================================================
   LOGOUT
========================================================= */

function logout() {

    const t = token();

    if (t) {

        fetch(
            API + '/auth/logout',
            {
                method: 'POST',
                headers: auth()
            }
        ).catch(() => {});

    }


    localStorage.removeItem('jobsphereToken');
    localStorage.removeItem('jobsphereUser');

    location.href = 'login.html';

}


/* =========================================================
   ESCAPE HTML
========================================================= */

function esc(s) {

    return String(s ?? '').replace(
        /[&<>'"]/g,
        x => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            "'": '&#39;',
            '"': '&quot;'
        }[x])
    );

}


/* =========================================================
   NOTICE
========================================================= */

function notice(el, text, ok = false) {

    el = typeof el === 'string' ? $(el) : el;

    if (!el) return;

    el.className =
        'notice show ' +
        (ok ? 'ok' : 'error');

    el.textContent = text;

}


/* =========================================================
   API JSON
========================================================= */

async function apiJson(url, options = {}) {

    const r =
        await fetch(
            API + url,
            options
        );


    const j =
        await r.json().catch(
            () => ({
                success: false,
                message: `Request failed (${r.status})`
            })
        );


    if (!r.ok || j.success === false) {

        throw new Error(
            j.message ||
            `Request failed (${r.status})`
        );

    }


    return j;

}


/* =========================================================
   PRETTY JSON
========================================================= */

function prettyJson(s) {

    try {

        return typeof s === 'string'
            ? JSON.parse(s)
            : s || {};

    } catch {

        return {
            summary:
                String(
                    s ||
                    'AI returned an unreadable response.'
                )
        };

    }

}


/* =========================================================
   GLOBAL THEME
========================================================= */

function applyJobsphereTheme(theme) {

    if (!theme) {

        theme =
            localStorage.getItem(
                'jobsphereTheme'
            ) || 'system';

    }


    document.body.classList.remove(
        'dark-mode'
    );


    /* DARK */

    if (theme === 'dark') {

        document.body.classList.add(
            'dark-mode'
        );

    }


    /* SYSTEM */

    else if (theme === 'system') {

        const systemDark =
            window.matchMedia(
                '(prefers-color-scheme: dark)'
            ).matches;


        if (systemDark) {

            document.body.classList.add(
                'dark-mode'
            );

        }

    }


    /* Save locally */

    localStorage.setItem(
        'jobsphereTheme',
        theme
    );

}


/* =========================================================
   LOAD SAVED THEME
========================================================= */

function loadJobsphereTheme() {

    const theme =
        localStorage.getItem(
            'jobsphereTheme'
        ) || 'system';


    applyJobsphereTheme(theme);

}


/* =========================================================
   SYSTEM THEME LISTENER
========================================================= */

if (
    window.matchMedia
) {

    window
        .matchMedia(
            '(prefers-color-scheme: dark)'
        )
        .addEventListener(
            'change',
            function () {

                const currentTheme =
                    localStorage.getItem(
                        'jobsphereTheme'
                    ) || 'system';


                if (
                    currentTheme === 'system'
                ) {

                    applyJobsphereTheme(
                        'system'
                    );

                }

            }
        );

}


/* =========================================================
   APPLY THEME IMMEDIATELY
========================================================= */

loadJobsphereTheme();


/* =========================================================
   SHELL
========================================================= */

function shell(title, active) {

    const u = user();

    const admin =
        u?.role === 'ADMIN';


    return `

        <aside class="side">

            <div class="brand">
                JOB<span>SPHERE</span>
            </div>


            <div class="side-user">

                <div class="avatar">

                    ${esc(
                        (u?.fullName || 'U')
                            .slice(0, 1)
                            .toUpperCase()
                    )}

                </div>


                <div>

                    <b>
                        ${esc(
                            u?.fullName || 'User'
                        )}
                    </b>

                    <small>
                        ${esc(
                            u?.role === 'ADMIN'
                                ? 'Administrator'
                                : 'Career workspace'
                        )}
                    </small>

                </div>

            </div>


            <nav>

                <a
                    class="${active === 'dashboard' ? 'active' : ''}"
                    href="dashboard.html"
                >
                    <span>⌂</span>
                    Dashboard
                </a>


                <a
                    class="${active === 'chat' ? 'active' : ''}"
                    href="ai-chat.html"
                >
                    <span>✦</span>
                    AI Chat
                </a>


                <a
                    class="${active === 'profile' ? 'active' : ''}"
                    href="profile.html"
                >
                    <span>◉</span>
                    My Profile
                </a>


                <a
                    class="${active === 'career' ? 'active' : ''}"
                    href="career-advisor.html"
                >
                    <span>✦</span>
                    AI Career Advisor
                </a>


                <a
                    class="${active === 'resume' ? 'active' : ''}"
                    href="resume-analyzer.html"
                >
                    <span>▣</span>
                    Resume Analyzer
                </a>


                <a
                    class="${active === 'interview' ? 'active' : ''}"
                    href="interview-simulator.html"
                >
                    <span>◉</span>
                    Interview Simulator
                </a>


                <a
                    class="${active === 'courses' ? 'active' : ''}"
                    href="courses.html"
                >
                    <span>▣</span>
                    Courses
                </a>


                <a
                    class="${active === 'certificates' ? 'active' : ''}"
                    href="certificates.html"
                >
                    <span>◇</span>
                    Certificates
                </a>


                <a
                    class="${active === 'executives' ? 'active' : ''}"
                    href="executives.html"
                >
                    <span>♟</span>
                    Industry Mentors
                </a>


                <a
                    class="${active === 'settings' ? 'active' : ''}"
                    href="settings.html"
                >
                    <span>⚙</span>
                    Settings
                </a>


                <a
                    class="${active === 'help' ? 'active' : ''}"
                    href="help-center.html"
                >
                    <span>?</span>
                    Help Center
                </a>


                <a
                    class="${active === 'feedback' ? 'active' : ''}"
                    href="feedback.html"
                >
                    <span>★</span>
                    Feedback
                </a>


                <a
                    class="${active === 'Contact' ? 'active' : ''}"
                    href="contact.html"
                >
                    <span>◉</span>
                    Contact Admin
                </a>


                ${
                    admin
                    ?
                    `

                    <div class="nav-label">
                        ADMINISTRATION
                    </div>

                    <a
                        class="${active === 'admin' ? 'active' : ''}"
                        href="admin-dashboard.html"
                    >
                        <span>⚙</span>
                        Admin Center
                    </a>

                    `
                    :
                    ''
                }

            </nav>


            <a
                class="logout"
                href="#"
                onclick="logout();return false"
            >
                <span>↪</span>
                Sign out
            </a>

        </aside>


        <main class="main">

            <div class="top">

                <div>

                    <div class="eyebrow">
                        JOBSPHERE / ${esc(title)}
                    </div>

                    <h1>
                        ${esc(title)}
                    </h1>

                </div>


                <div class="top-actions">

                    <button
                        class="mobile-menu"
                        onclick="document.querySelector('.side').classList.toggle('open')"
                    >
                        ☰
                    </button>

                    <span class="pill">
                        ${esc(
                            u?.role || 'USER'
                        )}
                    </span>

                </div>

            </div>

    `;

}


/* =========================================================
   CLOSE SHELL
========================================================= */

function closeShell() {

    return '</main>';

}


/* =========================================================
   STAT CARD
========================================================= */

function statCard(
    label,
    value,
    sub = ''
) {

    return `

        <div class="card stat-card">

            <div class="stat-label">
                ${esc(label)}
            </div>

            <div class="stat-value">
                ${esc(value)}
            </div>

            <div class="stat-sub">
                ${esc(sub)}
            </div>

        </div>

    `;

}


/* =========================================================
   FOOTER
========================================================= */

function dashboardFooter() {

    return `

        <footer class="jobsphere-footer">

            <div class="footer-container">


                <!-- BRAND -->

                <div class="footer-column footer-brand">

                    <div class="footer-logo">

                        <span class="logo-icon">
                            <i class="fa-solid fa-briefcase"></i>
                        </span>

                        <span>
                            JOB<span>SPHERE</span>
                        </span>

                    </div>


                    <p>

                        AI-powered career guidance and professional
                        development platform helping you discover
                        opportunities, build your skills and achieve
                        your career goals.

                    </p>


                    <div class="footer-social">

                        <a href="#" aria-label="LinkedIn">
                            <i class="fa-brands fa-linkedin-in"></i>
                        </a>

                        <a href="#" aria-label="GitHub">
                            <i class="fa-brands fa-github"></i>
                        </a>

                        <a href="#" aria-label="Facebook">
                            <i class="fa-brands fa-facebook-f"></i>
                        </a>

                        <a href="#" aria-label="Instagram">
                            <i class="fa-brands fa-instagram"></i>
                        </a>

                    </div>

                </div>


                <!-- PLATFORM -->

                <div class="footer-column">

                    <h3>
                        Platform
                    </h3>

                    <a href="dashboard.html">
                        Dashboard
                    </a>

                    <a href="profile.html">
                        My Profile
                    </a>

                    <a href="career-advisor.html">
                        AI Career Advisor
                    </a>

                    <a href="resume-analyzer.html">
                        Resume Analyzer
                    </a>

                </div>


                <!-- CAREER TOOLS -->

                <div class="footer-column">

                    <h3>
                        Career Tools
                    </h3>

                    <a href="career-advisor.html">
                        Career Guidance
                    </a>

                    <a href="resume-analyzer.html">
                        Resume Analysis
                    </a>

                    <a href="interview-simulator.html">
                        Interview Simulator
                    </a>

                    <a href="courses.html">
                        Courses
                    </a>

                    <a href="executives.html">
                        Industry Mentors
                    </a>

                    <a href="help-center.html">
                        Help Center
                    </a>

                    <a href="resume-builder.html">
                        Resume Builder
                    </a>

                    <a href="profile.html">
                        Career Profile
                    </a>

                </div>


                <!-- SUPPORT -->

                <div class="footer-column">

                    <h3>
                        Support
                    </h3>

                    <a href="contact.html">
                        Contact Us
                    </a>

                    <a href="contact.html">
                        Help & Support
                    </a>

                    <a href="contact.html">
                        Send Feedback
                    </a>

                </div>

            </div>


            <!-- FOOTER BOTTOM -->

            <div class="footer-bottom">

                <div class="footer-bottom-container">

                    <p>
                        © 2026 JOBSPHERE. All rights reserved.
                    </p>


                    <div class="footer-legal">

                        <a href="#">
                            Privacy Policy
                        </a>

                        <span>|</span>

                        <a href="#">
                            Terms &amp; Conditions
                        </a>

                    </div>

                </div>

            </div>

        </footer>

    `;

}