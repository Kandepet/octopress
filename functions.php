<?php

// Path constants
define('THEME', get_bloginfo('template_url'), true);
define('THEME_JS', THEME . '/js/', true);

add_theme_support( 'qa_style' );

if (function_exists('register_sidebar'))
    register_sidebar(2);

function new_excerpt_more($more) {
       global $post;
	return '<a href="'. get_permalink($post->ID) . '"> [Read the Rest...]</a>';
}
add_filter('excerpt_more', 'new_excerpt_more');

function theme_load_js() {
    if (is_admin()) return;
    wp_enqueue_script('jquery');
    wp_enqueue_script('nav', THEME_JS .'jquery.dropdown.js', array('jquery'));

}
add_action('init', theme_load_js);

function cleanr_theme_comment($comment, $args, $depth) {
   $GLOBALS['comment'] = $comment; ?>
   <li <?php comment_class(); ?> id="li-comment-<?php comment_ID() ?>">
    
     <div id="comment-<?php comment_ID(); ?>">
     <div class="commenthead">
      <div class="comment-author vcard">
         <?php echo get_avatar($comment,$size='36',$default='<path_to_url>' ); ?>

         <?php printf(__('<cite class="fn">%s</cite>'), get_comment_author_link()) ?>
      </div>
      

      <div class="comment-meta commentmetadata"><a href="<?php echo htmlspecialchars( get_comment_link( $comment->comment_ID ) ) ?>"><?php printf(__('%1$s at %2$s'), get_comment_date(),  get_comment_time()) ?></a><?php edit_comment_link(__('(Edit)'),'  ','') ?>
     
     <?php if ($comment->comment_approved == '0') : ?>
         <em><?php _e('...awaiting moderation') ?></em>
         <br />
      <?php endif; ?>
      </div>
      <div class="clear"></div>
     
     </div>
     

	<div class="commentbody">
      <?php comment_text() ?>

      <div class="reply">
         <?php comment_reply_link(array_merge( $args, array('depth' => $depth, 'max_depth' => $args['max_depth']))) ?>
      </div>
     </div>
     </div>
<?php } ?>



<?php
// Advertising 125x125px
class QandA_Adverts extends WP_Widget {
	function QandA_Adverts() {
		$widget_ops = array( 'classname' => 'widget_sofa_adverts', 'description' => 'Add 125x125px advertising units.' );
		$control_ops = array( 'width' => 640, 'height' => 400 );
		$this->WP_Widget( 'sofa_adverts', 'QandA Adverts', $widget_ops, $control_ops );
	}
 
	function widget( $args, $instance ) {
		extract( $args, EXTR_SKIP );
		
		$s_adverts_title = empty( $instance[ 's_adverts_title' ] ) ? __( "Advertising", "sofa_qanda" ) : apply_filters( 'widget_s_adverts_title', $instance[ 's_adverts_title' ] );
		$s_adverts_code  = empty( $instance[ 's_adverts_code' ] ) ? '' : apply_filters( 'widget_s_adverts_code', $instance[ 's_adverts_code' ] );
		
		echo $before_widget;
		echo $before_title . $s_adverts_title . $after_title;
		echo '<div class="advertz">';
		echo $s_adverts_code;
		echo '</div>';
		echo $after_widget;
	}
 
	function update( $new_instance, $old_instance ) {
		$instance = $old_instance;
		$instance[ 's_adverts_title' ] = strip_tags( $new_instance[ 's_adverts_title' ] );
		$instance[ 's_adverts_code' ]  = $new_instance[ 's_adverts_code' ];
		return $instance;
	}
 
	function form( $instance ) {
		$instance = wp_parse_args( ( array ) $instance, array( 's_adverts_title' => '', 's_adverts_code' => '' ) );
		$s_adverts_title = strip_tags( $instance[ 's_adverts_title' ] );
		$s_adverts_code  = $instance[ 's_adverts_code' ];
?>

<p>
<label for="<?php echo $this->get_field_id( 's_adverts_title' ); ?>">
Title: 
<input class="widefat" id="<?php echo $this->get_field_id( 's_adverts_title' ); ?>" name="<?php echo $this->get_field_name( 's_adverts_title' ); ?>" type="text" value="<?php echo attribute_escape( $s_adverts_title ); ?>" />
</label>
</p>

<label for="<?php echo $this->get_field_id( 's_adverts_code' ); ?>">
Advertising code (HTML):
</label>
<textarea class="widefat" rows="16" cols="50" id="<?php echo $this->get_field_id( 's_adverts_code' ); ?>" name="<?php echo $this->get_field_name( 's_adverts_code' ); ?>" type="text" value="<?php echo attribute_escape( $s_adverts_code ); ?>"><?php echo $s_adverts_code; ?></textarea>
            
<?php
	}
}
register_widget( 'QandA_Adverts' );



class QandA_New_User_Text extends WP_Widget {
	function QandA_New_User_Text() {
		$widget_ops = array( 'classname' => 'widget_sofa_stats', 'description' => 'Display New User Text.' );
		$this->WP_Widget( 'sofa_stats', 'QandA New User Text', $widget_ops );
	}
 
	function widget( $args, $instance ) {
		extract( $args, EXTR_SKIP );
 
		echo $before_widget;
		
		$s_stats_title = empty( $instance[ 's_stats_title' ] ) ? '' : apply_filters( 'widget_s_stats_title', $instance[ 's_stats_title' ] );

		if( $s_stats_title ) echo $before_title . $s_stats_title . $after_title;
		
		global $wpdb;

        echo '<section class="first odd">';
        /*echo '<br>';
        echo '<div class="search">';
            echo '<form action="/" id="search-form" class="search-form" method="get">';
                echo '<fieldset>';
                echo '<input type="text" placeholder="Search" value="" name="s" class="text-input" id="search-text">';
                echo '<input type="submit" value="Go" name="submit" class="search-submit" id="search-submit"> ';
                echo '</fieldset> ';
            echo '</form>';
                echo '</div>';*/

        echo '<h1>About Hacker Labs</h1>';
        echo '<p>Hacker labs brings the latest innovations, technologies and conversations from around the web. </p>';
        echo '<div class="search">';
            echo '<form action="/" id="search-form" class="search-form" method="get">';
                echo '<fieldset>';
                echo '<input type="text" placeholder="Search" value="" name="s" class="text-input" id="search-text">';
                echo '<input type="submit" value="Go" name="submit" class="search-submit" id="search-submit"> ';
                echo '</fieldset> ';
            echo '</form>';
        echo '</div>';

?>
<div class="social-icons-container">
<a target="_blank" rel="nofollow" href="http://facebook.com/HackerLabs"><img alt="Facebook" title="Facebook" src="<?php echo get_bloginfo('template_url') ?>/images/icons/24x24/facebook.png"></a>
<a target="_blank" rel="nofollow" href="http://twitter.com/HackerLabs"><img alt="Twitter" title="Twitter" src="<?php echo get_bloginfo('template_url') ?>/images/icons/24x24/twitter.png"></a>
<a target="_blank" rel="nofollow" href="https://plus.google.com/u/0/b/101352013442440682418/"><img alt="Google+" title="Google+" src="<?php echo get_bloginfo('template_url') ?>/images/icons/24x24/google-plus.png"></a>
<a target="_blank" rel="nofollow" href="http://github.com/HackerLabs"><img alt="github" title="github" src="<?php echo get_bloginfo('template_url') ?>/images/icons/24x24/github.png"></a>
<a target="_blank" rel="nofollow" href="Feeds"><img alt="RSS Feed" title="RSS Feed" src="<?php echo get_bloginfo('template_url') ?>/images/icons/24x24/rss.png"></a>
<a target="_blank" rel="nofollow" href="http://delicious.com/HackerLabs"><img alt="Delicious" title="Delicious" src="<?php echo get_bloginfo('template_url') ?>/images/icons/24x24/delicious.png"></a>
</div>

<?php
                      echo '</section>';

		
		echo $after_widget;
		
	}
 
	function update( $new_instance, $old_instance ) {
		$instance = $old_instance;
		$instance[ 's_stats_title' ] = strip_tags( $new_instance[ 's_stats_title' ] );
 
		return $instance;
	}
 
	function form( $instance ) {
		$instance = wp_parse_args( ( array ) $instance, array( 's_stats_title' => '' ) );
		$s_stats_title = strip_tags( $instance[ 's_stats_title' ] );
?>

<p>
<label for="<?php echo $this->get_field_id( 's_stats_title' ); ?>">
Title: 
<input class="widefat" id="<?php echo $this->get_field_id( 's_stats_title' ); ?>" name="<?php echo $this->get_field_name( 's_stats_title' ); ?>" type="text" value="<?php echo attribute_escape( $s_stats_title ); ?>" />
</label>
</p>
            
<?php
	}
}

register_widget( 'QandA_New_User_Text' );


class GHGPostWidget extends WP_Widget {
  function GHGPostWidget()
  {
    parent::WP_Widget(false, 'Recent Posts Widget Unlimited');
  }
  function form($instance)
  {
    /* Set up some default widget settings. */
    $defaults = array( 'title' => 'Recent Posts', 'num_posts' => '15');
    $instance = wp_parse_args( (array) $instance, $defaults ); ?>
    <p>
      <label for="<?php echo $this->get_field_id('title'); ?>"><?php _e('Recent Posts'); ?></label>
      <input id="<?php echo $this->get_field_id('title'); ?>" name="<?php echo $this->get_field_name('title'); ?>" type="text" value="<?php echo $instance['title']; ?>" style="width:100%"/>
    </p>
    <p>
      <label for="<?php echo $this->get_field_id('num_posts'); ?>">Number of posts to show:</label>
      <input id="<?php echo $this->get_field_id('num_posts'); ?>" name="<?php echo $this->get_field_name('num_posts'); ?>" value="<?php echo $instance['num_posts']; ?>" style="width:40px;" />
    </p>
<?php

  }
  function update($new_instance, $old_instance)
  {
    return $new_instance;
  }
  function widget($args, $instance)
  {
    extract( $args );

    /* User-selected settings. */
    $title = apply_filters('widget_title', $instance['title'] );
    $num_posts = $instance['num_posts'];

    /* Before widget (defined by themes). */
    echo $before_widget;

    /* Title of widget (before and after defined by themes). */
    //if ( $title ) echo $before_title . $title . $after_title;

    GHGPostWidget::getRecentPosts($num_posts);

    /* After widget (defined by themes). */
    echo $after_widget;

  }
  function getRecentPosts($num_posts)
  {
    global $wpdb;
    $sql = "select * from ".$wpdb->posts." where post_status='publish' and post_type='post' order by post_date desc limit ".$num_posts;
    $posts = $wpdb->get_results($sql);
    if (count($posts) >= 1 )
    {
      $postArray = array();
      foreach ($posts as $post)
      {
        wp_cache_add($post->ID, $post, 'posts');
        $postArray[] = array('title' => stripslashes($post->post_title), 'url' => get_permalink($post->ID));
      }


      echo '<section class="even">';
            echo '<h1>Recent Posts</h1>';
              echo '<ul id="recent_posts">';
                  

      foreach ($postArray as $post)
      {
        echo '<li class="post">';
        echo '<a href="'.$post['url'].'" title="'.$post['title'].'">'.$post['title'].'</a>';
        echo '</li>';
      }
    echo '</ul>';
    echo '</section>';
    }
  }
}

register_widget('GHGPostWidget');


?>
