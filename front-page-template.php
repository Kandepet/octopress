<?php

/*
Template Name: front-page
*/

$file = dirname( __FILE__ );
require( $file . '/../../../wp-load.php' );

get_header(); ?>

        <div id="main">
            <div id="frontpage-content">
            <!--<div id="content">-->

    <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
            <div class="blog-index">
<?php //if( function_exists('FA_display_slider') ){ FA_display_slider(110); } ?>

<hr>
<p> My name is Deepak Kandepet. Hacker Labs is my playground for experimenting with the latest innovations, technologies and products. </p>

<?php /*
<div class="center-block">

            <!-- START "Who We Are" Block -->
            <div class="one_third_home border-left">
                <h3>Who We Are?</h3>
                <span class="number-block"> </span>                
                <p>
                    We are a small group of hackers who like to build things. By profession we are search backend and relevance engineers working for one of the best search engine company. We are also passionate about web services and frontend design. 
               </p>
               <a class="button right" href="#">Read more &mdash; </a>
            </div> <!-- end "Who We Are" Block -->
            
            <!-- START "What We Do" Block -->
            <div class="one_third_home border-left">
                <h3>What We Do?</h3>
                <span class="number-block"> </span>
                <p>
                    We create web services, both front-end and backend, using the latest technologies.
                    All our works contain valid code, and a clean design that is chosen solely for your tasks.
                    See our <a href="portfolio.html">Portfolio</a> for what we have done.               
                </p>
               <a class="button right" href="#">Read more &mdash; </a>                
            </div> <!-- end "What We Do" Block -->
            
            <!-- START "We And Social Services" Block -->
            <div class="one_third_home border-left column-last">
                <h3>Want something built?</h3>
                <span class="number-block"> </span>
                <p>
                    Get in touch with us :
               </p>
               
               <!-- START Social Icons -->
               <ul id="social-icons">
                   <li><a class="twitter" href="http://twitter.com/HackerLabs"></a></li>
                   <li><a class="facebook" href="http://facebook.com/HackerLabs"></a></li>
                   <!--<li><a class="skype" href="http://skype.com"></a></li>
                   <li><a class="behance" href="http://rss.com"></a></li>
                   <li><a class="linkedin" href="http://linkedin.com"></a></li>-->
                   <li><a class="gplus" href="http://vimeo.com"></a></li>
                   <li><a class="email" href="http://vimeo.com"></a></li>
               </ul> <!-- end #social-icons -->
                   
            </div> <!-- end "We And Social Services" Block -->

        <div class="clear"></div>
    </div>

 */ ?>


                <hr>
<div id="main">
                <h2 class="title"> Recent work </h2>
                <ul class="three_column home">
                    <li>
                        <a href="http://prep.ly/"> <img class="imgborder" alt="" src="http://hackerlabs.co/wp-content/themes/octopress/images/thumbs/preply-screen-capture.PNG" width="280" height="138"> </a>
                        <h4><a href="http://prep.ly/">prep.ly</a></h4>
                        <p>Interview preparation tool with online practices and live mock interviews. Built using c, c++, php.</p>
                    </li>
                    <li>
                        <a href="http://trendwars.com/"> <img class="imgborder" alt="" src="http://hackerlabs.co/wp-content/themes/octopress/images/thumbs/TrendWars-screenshot.PNG" width="280" height="138"> </a>
                        <h4><a href="http://trendwars.com/">trendwars.com</a></h4>
                        <p>A twitter trend analyser game. Buit using php, python and twitter sentiment analysis (machine learning)</p>
                    </li>
    
                    <li class="last">
                        <a href="http://samefeather.com/"> <img class="imgborder" alt="" src="http://hackerlabs.co/wp-content/themes/octopress/images/thumbs/SameFeather-screenshot.PNG" width="280" height="138"> </a>
                        <h4><a href="http://samefeather.com/">samefeather.com</a></h4>
                        <p>A social network built using opensource technology.</p>
                    </li>
                </ul>

                <div class="clear"></div><!-- clear float -->
                <hr>
                <!--<div class="one_fourth home">
                    <h2 class="title">Service news </h2>
                    <?php //echo do_shortcode('[twitter_stream template="list" user="CoupaCafe" count=5]'); ?>
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut vitae risus dolor. Duis vehicula fermentum eros ut tempor.</p>
                    <ul class="bullet-check">
                        <li>Website &amp; Development System</li>
                        <li>Animation Photoshop</li>
                        <li>Network &amp; Web 2.1 Company</li>
                        <li>eCommerce &amp; Portfolio</li>
                        <li>Landingpage &amp; Branding</li>
                        <li>More...</li>
                    </ul>
                </div>-->
                <div class="three_fourth home last">
                    <h2 class="title">Recent Articles</h2>
                    <?php //rotating_posts(); ?>
                    <?php special_recent_posts(); ?>
                    <!--<ul class="recent">
                        <li><img class="alignleft imgborder" alt="" src="http://demo.templatesquare.com/html/perfekto/images/content/rp1.jpg">
                            <h5>Lorem ipsum</h5>
                            <p>Sed imperdiet tellus id risus rutrum nec feugiat ante pretium. Etiam massa arcu, molestie ac dapibus nec, posuere sit amet arcu. Phasellus cursus, dolor ac venenatis fermentum, metus sem pellentesque eros. <a href="#">Read more...</a></p>
                        </li>
                        <li><img class="alignleft imgborder" alt="" src="http://demo.templatesquare.com/html/perfekto/images/content/rp2.jpg">
                            <h5>Lorem ipsum</h5>
                            <p>Sed imperdiet tellus id risus rutrum nec feugiat ante pretium. Etiam massa arcu, molestie ac dapibus nec, posuere sit amet arcu. Phasellus cursus, dolor ac venenatis fermentum, metus sem pellentesque eros. <a href="#">Read more...</a></p>
                        </li>
                    </ul>-->
                </div>
                <div class="clear"></div><!-- clear float -->
            </div>


                <article class="hentry">
                    <!--<header>
                    <h1 class="entry-title"><a href="<?php the_permalink() ?>"><?php the_title(); ?></a></h1>
                    </header>-->

				<!--<div class="entry">-->
                <div class="entry-content"> <?php the_content(); ?> </div>
                    </article>

			</div>
	<?php endwhile; endif; ?>
    <!--<aside role=sidebar> <?php //get_sidebar(); ?> </aside>-->
<?php //rotating_posts(); ?>

	</div>
</div>

<?php get_footer(); ?>
