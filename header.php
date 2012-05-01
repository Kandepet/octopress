<?php
/*
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" <?php language_attributes(); ?>>

<head profile="http://gmpg.org/xfn/11">
<meta http-equiv="Content-Type" content="<?php bloginfo('html_type'); ?>; charset=<?php bloginfo('charset'); ?>" />

<title><?php wp_title('&laquo;', true, 'right'); ?> <?php bloginfo('name'); ?></title>

<link rel="shortcut icon" href="/io/wp-content/themes/cleanr/images/favicon.ico" >
<link rel="stylesheet" href="<?php bloginfo('stylesheet_url'); ?>" type="text/css" media="screen" />
<!--[if IE]><link type="text/css" href="<?php bloginfo('template_directory'); ?>/css/ie.css" rel="stylesheet" media="all" /><![endif]-->
<link rel="alternate" type="application/rss+xml" title="<?php bloginfo('name'); ?> RSS Feed" href="<?php bloginfo('rss2_url'); ?>" />
<link rel="alternate" type="application/atom+xml" title="<?php bloginfo('name'); ?> Atom Feed" href="<?php bloginfo('atom_url'); ?>" />
<link rel="pingback" href="<?php bloginfo('pingback_url'); ?>" />

<?php wp_head(); ?>

</head>
<body>
<div class="container_16">
<div id="header" class="grid_16">
 <!--<h1><a href="<?php echo get_option('home'); ?>/"><?php bloginfo('name'); ?></a><span class="description"><?php bloginfo('description'); ?></span></h1>	-->
 <!--<h1><font color="#cc0000">High</font>Entropy <span class="description"><?php bloginfo('description'); ?></span></h1>	-->
 <h1><a href="<?php echo get_option('home'); ?>/"><font color="#cc0000">Hacker</font>Stack</a><span class="description"><?php bloginfo('description'); ?></span></h1>	
 <!--<h1><a href="<?php echo get_option('home'); ?>/"><span id="SiteTitleRed">High</span><img src="/io/wp-content/themes/cleanr/images/entropy.gif" width="20%" height="20%" align="middle"></a><span class="description"><?php bloginfo('description'); ?></span></h1>	-->

<ul id="nav">
  <li class="nav"><a title="Q & A" href="/questions/">Q & A</a></li>
  <li class="nav"><a title="About" href="/about/">About</a></li>
  <!--<?php wp_list_pages('title_li=Pages'); ?>
  <?php wp_list_categories('title_li=Categories'); ?>-->
  <!--<?php wp_list_pages('title_li=Archives'); ?>-->
  <li class="last-child"><a href="<?php bloginfo('rss2_url'); ?>" class="rss">RSS</a></li>
</ul>
  
</div>
<hr />

 */
?>







<!DOCTYPE html>
<!--[if IEMobile 7 ]><html class="no-js iem7"><![endif]-->
<!--[if lt IE 9]><html class="no-js lte-ie8"><![endif]-->
<!--[if (gt IE 8)|(gt IEMobile 7)|!(IEMobile)|!(IE)]><!-->
<html class="no-js" lang="en"><!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <title><?php wp_title('&laquo;', true, 'right'); ?> <?php bloginfo('name'); ?></title>
        <meta name="author" content="Deepak Kandepet">


        <!-- http://t.co/dKP3o1e -->
        <meta name="HandheldFriendly" content="True">
        <meta name="MobileOptimized" content="320">
        <meta name="viewport" content="width=device-width, initial-scale=1">


        <!-- Google Fonts -->
        <link href='http://fonts.googleapis.com/css?family=Bad+Script' rel='stylesheet' type='text/css' />
        <link href='http://fonts.googleapis.com/css?family=Audiowide' rel='stylesheet' type='text/css' />
        <link href='http://fonts.googleapis.com/css?family=Play' rel='stylesheet' type='text/css' />
        <link href='http://fonts.googleapis.com/css?family=Rock+Salt' rel='stylesheet' type='text/css' />
        <link href='http://fonts.googleapis.com/css?family=Condiment' rel='stylesheet' type='text/css' />


        <link rel="canonical" href="http://hackerlabs.co/blog/"/>
        <link href="<?php bloginfo('template_directory'); ?>/images/favicon.png" rel="shortcut icon" />
        <link href="<?php bloginfo('template_directory'); ?>/stylesheets/screen.css" media="screen, projection" rel="stylesheet" type="text/css">
        <script src="<?php bloginfo('template_directory'); ?>/javascripts/modernizr-2.0.js"></script>
        <script src="http://s3.amazonaws.com/ender-js/jeesh.min.js"></script>
        <script src="<?php bloginfo('template_directory'); ?>/javascripts/octopress.js" type="text/javascript"></script>
        <link href="/atom.xml" rel="alternate" title="Ramblings from the corner" type="application/atom+xml"/>
        <!--Fonts from Google's Web font directory at http://google.com/webfonts -->
        <link href='http://fonts.googleapis.com/css?family=PT+Serif:regular,italic,bold,bolditalic' rel='stylesheet' type='text/css'>
        <link href='http://fonts.googleapis.com/css?family=PT+Sans:regular,italic,bold,bolditalic' rel='stylesheet' type='text/css'>
        <link rel="alternate" type="application/rss+xml" title="<?php bloginfo('name'); ?> RSS Feed" href="<?php bloginfo('rss2_url'); ?>" />
        <link rel="alternate" type="application/atom+xml" title="<?php bloginfo('name'); ?> Atom Feed" href="<?php bloginfo('atom_url'); ?>" />
        <link rel="pingback" href="<?php bloginfo('pingback_url'); ?>" />

        <?php wp_head(); ?>


    </head>

    <body  >
        <header>
        <hgroup>
        <!--<h1><a href="/">Ramblings from the corner</a></h1>-->
        <!--<h1><a href="<?php echo get_option('home'); ?>/"><?php bloginfo('name'); ?></a></h1>-->
        <h1><a href="/"><?php bloginfo('name'); ?></a></h1>

        <h2><?php bloginfo('description'); ?></h2>

        </hgroup>
        </header>

        <nav role=navigation>
        <!--<ul role=subscription data-subscription="rss">
            <li><a href="/atom.xml" rel="subscribe-rss" title="subscribe via RSS">RSS</a></li>

        </ul>-->
        <?php //if(is_front_page()) { get_search_form(); } ?>
        <?php /*if(is_front_page()) { ?>

        <form method="get" action="http://hackerlabs.co/blog">
            <fieldset role="site-search">
                <input type="hidden" value="site:hackerlabs.co" name="q">
                <input type="text" placeholder="Search" results="0" name="s" class="search">
            </fieldset>
            <fieldset role="mobile-nav">
            <select>
                <option value="">Navigate…</option>
                <option value="http://hackerlabs.co/blog/about">• About</option>
                <option value="http://hackerlabs.co/blog/products">• Products</option>
                <option value="http://hackerlabs.co/blog/blog">• Blog</option>
            </select>
            </fieldset>
        </form>

        <?php } */ ?>
<!--
        <form action="http://google.com/search" method="get">
            <fieldset role="site-search">
                <input type="hidden" name="q" value="site:rtgibbons.com" />
                <input class="search" type="text" name="q" results="0" placeholder="Search"/>
            </fieldset>
        </form>
-->
        <ul role=main-navigation>
        <?php if(is_front_page()) { ?>
            <!--<li><a href="/">Home</a></li>-->
            <li><a href="/blog/about">About</a></li>
            <li><a href="/blog/portfolio">Portfolio</a></li>
            <li><a href="/blog/hacks">Hacks</a></li>
            <li><a href="/blog">Blog</a></li>
        <?php } else { ?>
            <?php //wp_list_pages('title_li='); ?>
            <!--<li><a href="/">Home</a></li>-->
            <li><a href="/blog/about">About</a></li>
            <li><a href="/blog/portfolio">Portfolio</a></li>
            <li><a href="/blog/hacks">Hacks</a></li>
            <li><a href="/blog/blog">Blog</a></li>
            <li><a href="/blog/archive">Archive</a></li>
        </ul>
        <?php } ?>

        </nav>



