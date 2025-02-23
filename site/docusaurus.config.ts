import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import {themes} from 'prism-react-renderer';
import remarkMath from 'remark-math';
import rehypeKatex from 'rehype-katex';

const config: Config = {
    title: 'MEDia',
    tagline: 'Blog about mathematics and computer experiments',
    favicon: '/img/logos/MEDia-logo-themed.svg',

    url: 'https://lounres.dev',
    baseUrl: '/MEDia/',

    onBrokenLinks: 'warn',
    onBrokenAnchors: 'warn',
    onBrokenMarkdownLinks: 'warn',
    onDuplicateRoutes: 'warn',

    i18n: {
        defaultLocale: 'en',
        locales: ['en', 'ru'],
        localeConfigs: {
            en: {
                label: 'English',
                direction: 'ltr',
                htmlLang: 'en-GB',
                calendar: 'gregory',
                path: 'en',
            },
            ru: {
                label: 'Русский',
                direction: 'ltr',
                htmlLang: 'ru',
                calendar: 'gregory',
                path: 'ru',
            },
        },
    },

    plugins: [
        [
            '@docusaurus/plugin-content-docs',
            {
                id: 'experiments',
                path: 'experiments',
                routeBasePath: 'experiments',
                sidebarPath: './experimentsSidebars.ts',
                remarkPlugins: [remarkMath],
                rehypePlugins: [rehypeKatex],
            },
        ]
    ],

    presets: [
        [
            'classic',
            {
                docs: {
                    id: 'docs',
                    path: 'docs',
                    routeBasePath: 'docs',
                    sidebarPath: './docsSidebars.ts',
                    remarkPlugins: [remarkMath],
                    rehypePlugins: [rehypeKatex],
                },
                blog: {
                    routeBasePath: '/',
                    showReadingTime: true,
                    feedOptions: {
                        type: ['rss', 'atom'],
                        xslt: true,
                    },
                    onInlineTags: 'warn',
                    onInlineAuthors: 'warn',
                    onUntruncatedBlogPosts: 'warn',
                    remarkPlugins: [remarkMath],
                    rehypePlugins: [rehypeKatex],
                },
                theme: {
                    customCss: './src/css/custom.css',
                },
            } satisfies Preset.Options,
        ],
    ],

    stylesheets: [
        {
            href: 'https://cdn.jsdelivr.net/npm/katex@0.16.21/dist/katex.min.css',
            type: 'text/css',
            integrity: 'sha384-zh0CIslj+VczCZtlzBcjt5ppRcsAmDnRem7ESsYwWwg3m/OaJ2l4x7YBZl9Kxxib',
            crossorigin: 'anonymous',
        },
    ],

    themeConfig: {
        // Replace with your project's social card
        image: '/img/logos/MEDia-logo-light-theme.svg', // TODO: Add social card
        // metadata: [],
        // announcementBar: {},
        docs: {
            sidebar: {
                hideable: true,
            }
        },
        navbar: {
            title: 'MEDia',
            logo: {
                alt: 'MEDia blog Logo',
                src: '/img/logos/MEDia-logo-light-theme.svg',
                srcDark: '/img/logos/MEDia-logo-dark-theme.svg',
            },
            items: [
                {
                    type: 'docSidebar',
                    sidebarId: 'experiments',
                    position: 'left',
                    label: 'Experiments',
                    docsPluginId: 'experiments',
                },
                {
                    type: 'docSidebar',
                    sidebarId: 'docs',
                    position: 'left',
                    label: 'Docs',
                    docsPluginId: 'docs',
                },
                {
                    type: 'localeDropdown',
                    position: 'right',
                },
            ],
        },
        footer: {
            style: 'dark',
            // links: [
            //   {
            //     title: 'Docs',
            //     items: [
            //       {
            //         label: 'Tutorial',
            //         to: '/docs/intro',
            //       },
            //     ],
            //   },
            //   {
            //     title: 'Community',
            //     items: [
            //       {
            //         label: 'Stack Overflow',
            //         href: 'https://stackoverflow.com/questions/tagged/docusaurus',
            //       },
            //       {
            //         label: 'Discord',
            //         href: 'https://discordapp.com/invite/docusaurus',
            //       },
            //       {
            //         label: 'X',
            //         href: 'https://x.com/docusaurus',
            //       },
            //     ],
            //   },
            //   {
            //     title: 'More',
            //     items: [
            //       {
            //         label: 'Blog',
            //         to: '/blog',
            //       },
            //       {
            //         label: 'GitHub',
            //         href: 'https://github.com/facebook/docusaurus',
            //       },
            //     ],
            //   },
            // ],
            copyright: `Copyright © ${new Date().getFullYear()} Gleb Minaev <br> All rights reserved. Licensed under the Apache License, Version 2.0 <br> Built with Docusaurus.`,
        },
        prism: {
            theme: themes.github,
            darkTheme: themes.dracula,
        },
    } satisfies Preset.ThemeConfig,
};

export default config;
